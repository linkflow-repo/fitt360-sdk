package com.linkflow.fitt360sdk.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.adapter.BTDeviceRecyclerAdapter;
import com.linkflow.fitt360sdk.item.BTItem;

import app.library.linkflow.ConnectManager;
import app.library.linkflow.connect.BTConnectHelper;
import app.library.linkflow.connect.ConnectHelper;
import app.library.linkflow.connect.WifiConnectHelper;
import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.neckband.ConnectStateManage;

public class BTListActivity extends BaseActivity implements BTDeviceRecyclerAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        BTConnectHelper.Listener, ConnectManager.Listener {
    public static final int CALLED_BY_START_ACTIVITY = 1001, CALLED_BY_SETTING_ACTIVITY = 1002;
    private static final int MSG_P2P_STATE_DISABLED = 10;
    public static final String KEY_CALLED_BY = "calledBy";
    public static final String ACTION_BT_CONNECTED = "linkflow.app.fitt360.BT_CONNECTED";
    private int mCalledBy;

    private SwipeRefreshLayout mRefreshLayout;
    private ProgressBar mProgressBar;
    private BTDeviceRecyclerAdapter mAdapter;
    private BluetoothDevice mSelectedBTDevice;

    private ConnectHelper mConnectHelper;
    private BTConnectHelper mBTConnectHelper;
    private WifiConnectHelper mWifiConnectHelper;
    private boolean mCanceledBTDiscovery;

    private int mBeforeClickedItemPosition = -1;
    private ConnectManager mConnector;
    private WifiManager mWifiManager;
    private Handler mP2PStateChangeHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enableAlert(false);
        super.onCreate(savedInstanceState);
        setHeaderView(R.layout.header_bt_list);
        setBodyView(R.layout.activity_bt_list);

        onNewIntent(getIntent());

        mConnector = ConnectManager.getInstance(getApplicationContext()).setListener(this);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mP2PStateChangeHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_P2P_STATE_DISABLED) {
                    if (!mWifiManager.isWifiEnabled()) {
                        Toast.makeText(BTListActivity.this, R.string.wifi_state_disabled, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mConnectHelper = ConnectHelper.getInstance().init(this);
        mBTConnectHelper = mConnectHelper.getBTConnectHelper();
        mWifiConnectHelper = mConnectHelper.getWifiConnectHelper();

        mRefreshLayout = findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(this);

        TextView skipBtn = findViewById(R.id.skip_btn);
        skipBtn.setText(mCalledBy == CALLED_BY_START_ACTIVITY ? R.string.skip : R.string.back);
        skipBtn.setOnClickListener(this);

        mProgressBar = findViewById(R.id.circular);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new BTDeviceRecyclerAdapter(this, this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.addItems(mBTConnectHelper.getBondedBTList());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.skip_btn) {
            if (mCalledBy == CALLED_BY_START_ACTIVITY) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBTConnectHelper.registerReceiver(getApplicationContext());
        mBTConnectHelper.startDiscovery(-1, true, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCalledBy = intent.getIntExtra(KEY_CALLED_BY, -1);
    }

    @Override
    public void clickedItem(int position) {
        BTItem item = mAdapter.getItem(position);
        if (item.mState == BTItem.STATE_NEAR) {
            mAdapter.checked(mBeforeClickedItemPosition, position);
            mBeforeClickedItemPosition = position;
            mProgressBar.setVisibility(View.GONE);
            mBTConnectHelper.disconnect();
            mWifiConnectHelper.disconnect(true, -1,null);
            mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_NONE);

            mSelectedBTDevice = mAdapter.getItem(position).mDevice;
            if (mBTConnectHelper.isBondedDevice(mSelectedBTDevice)) {
                mConnector.start(null, mSelectedBTDevice);
            } else {
                Toast.makeText(this, R.string.bt_pairing_request, Toast.LENGTH_SHORT).show();
                mBTConnectHelper.selectedBTAddress(mSelectedBTDevice.getAddress());
                mSelectedBTDevice.createBond();
            }
        } else {
            Toast.makeText(this, item.mState == BTItem.STATE_FINDING ? R.string.bt_state_finding_alert : R.string.bt_state_none_alert, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void bluetoothState(boolean isOff) {

    }

    @Override
    public void foundBTDevice(BluetoothDevice device, boolean isCorrectDevice, boolean isBonded) {
        mAdapter.addItem(device);
        if (isCorrectDevice && isBonded) {
            int correctDevicePosition = mAdapter.getCorrectDevicePosition(device.getAddress());
            if (correctDevicePosition != -1) {
                mBeforeClickedItemPosition = correctDevicePosition;
                mAdapter.checked(-1, correctDevicePosition);
            }
        }
    }

    @Override
    public void changedBondedState(BluetoothDevice device, int state) {
        Log.e("btListActivity", "changed bonded state");
        if (state == BluetoothDevice.BOND_BONDED) {
            mAdapter.changedBonded(device.getAddress());
            mConnector.start(null, mSelectedBTDevice);
        } else if (state == BluetoothDevice.BOND_NONE) {
            Toast.makeText(this, R.string.bt_pairing_request_cancel, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void discoveryFinished(boolean foundDevice, boolean isBonded) {
        mProgressBar.setVisibility(View.GONE);
        if (!mCanceledBTDiscovery) {
            mAdapter.changeItemsStateNone();
        }
        mCanceledBTDiscovery = false;
    }

    @Override
    public void discoveryTryCnt(int tryCnt, int maxTryCnt, boolean foundTargetDevice) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAdapter.changeItemStateFinding();
    }

    @Override
    public void onRefresh() {
        mBeforeClickedItemPosition = -1;
        mRefreshLayout.setRefreshing(false);
        mAdapter.clear();
        mAdapter.addItems(mBTConnectHelper.getBondedBTList());

        mCanceledBTDiscovery = mBTConnectHelper.isDiscovering();

        mBTConnectHelper.startDiscovery(-1,true,this);
    }

    public BroadcastReceiver mConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && mCalledBy == CALLED_BY_START_ACTIVITY) {
                if (intent.getAction().equals(ACTION_BT_CONNECTED)) {
                    Intent mainIntent = new Intent(BTListActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }
    };

    @Override
    public void bluetoothState(ConnectManager.STATE state, ConnectManager.PARING paring) {
        switch (paring) {
            case REQUEST:
                Toast.makeText(this, R.string.bt_pairing_request, Toast.LENGTH_SHORT).show();
                break;
            case CANCELED:
                Toast.makeText(this, R.string.bt_pairing_request_cancel, Toast.LENGTH_LONG).show();
                break;
            case BONDED: break;
            case BONDED_NONE: break;
        }

        switch (state) {
            case STATE_CONNECTING:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT);
                Toast.makeText(this, R.string.bt_connecting, Toast.LENGTH_SHORT).show();
                break;
            case STATE_CONNECTED:
                Toast.makeText(this, R.string.bt_connected, Toast.LENGTH_SHORT).show();
                break;
            case STATE_DISCONNECTED:
                Toast.makeText(this, R.string.bt_disconnected, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void bluetoothDiscovery(int cnt, int max) {
        if (cnt > 4) {
            Toast.makeText(this, R.string.bt_not_found, Toast.LENGTH_LONG).show();
            mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT_NOT_FOUND);
        }
    }

    @Override
    public void p2pState(ConnectManager.STATE state, ConnectManager.STATUS status, WifiP2pInfo info) {
        switch (state) {
            case STATE_CONNECTING:
                Toast.makeText(this, R.string.wifi_p2p_connecting, Toast.LENGTH_SHORT).show();
                break;
            case STATE_CONNECTED:
                Toast.makeText(this, R.string.wifi_p2p_connected, Toast.LENGTH_SHORT).show();
                NeckbandRestApiClient.setBaseUrl(info.groupOwnerAddress.getHostAddress());
                mNeckbandManager.connect("newwifi", "123456");
                break;
            case STATE_DISCONNECTED:
                Toast.makeText(this, R.string.wifi_p2p_disconnected, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void p2pDiscovery(int cnt, boolean locationEnabled) {
        if (cnt > 4) {
            Toast.makeText(this, locationEnabled ? R.string.wifi_p2p_not_found_alert1 : R.string.wifi_state_disabled, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void step(ConnectManager.STEP step) {
        switch (step) {
            case STEP_NONE:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_NONE);
                break;
            case STEP_START: mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_START); break;
            case STEP_BT_FINDING: mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT_FINDING); break;
            case STEP_BT: mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT); break;
            case STEP_P2P: mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_P2P); break;
            case STEP_DONE: break;
        }
    }

    @Override
    public void alert(ConnectManager.ALERT alert) {
        switch (alert) {
            case ALERT_GPS_ENABLED:
                break;
            case ALERT_GPS_DISABLED:
                Toast.makeText(this, R.string.location_disable, Toast.LENGTH_SHORT).show();
                break;
            case ALERT_DISABLE:
                break;
            case ALERT_BT_OFF: Toast.makeText(this, R.string.bt_state_disabled, Toast.LENGTH_SHORT).show(); break;
            case ALERT_P2P_OFF:
                mP2PStateChangeHandler.removeMessages(MSG_P2P_STATE_DISABLED);
                mP2PStateChangeHandler.sendEmptyMessageDelayed(MSG_P2P_STATE_DISABLED, 1000);
                break;
        }
    }

    @Override
    public void restartAfterLocationEnabled() {
        mConnector.start(null, mSelectedBTDevice);
    }
}
