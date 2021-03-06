package com.biotronisis.pettplant.activity.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.content.res.Configuration;
import android.content.res.Resources;
//import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.biotronisis.pettplant.R;
import com.biotronisis.pettplant.activity.SettingsActivity;
import com.biotronisis.pettplant.debug.MyDebug;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class BluetoothScanFragment extends DialogFragment {

    private static final String TAG = "BluetoothScanFragment";
    private int orientation;
    private LayoutInflater inflater;
    private BluetoothAdapter bluetoothAdapter;
    private List<MyItem> items;
    private MyDeviceAdapter listAdapter;
    private MyBluetoothBroadcastReceiver bluetoothReceiver;
    private Context parentContext;
    private OnBluetoothDeviceSelectedListener listener;

    public void setContext(Context thisContext) {
        parentContext = thisContext;
    }

    public void setOnBluetoothDeviceSelectedListener(OnBluetoothDeviceSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (MyDebug.LOG) {
            Log.i(TAG, "------------ onCreateView ------------");
        }

        View view = inflater.inflate(R.layout.fragment_bluetooth_scan, container, false);

        this.inflater = inflater;

        // Hide the title.
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        items = new ArrayList<>();
        listAdapter = new MyDeviceAdapter();

        // Find and set up the ListView for paired devices
        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listAdapter);
//        Drawable cc = ContextCompat.getDrawable(getActivity(), R.drawable.layout_background_bluetooth);
//        listView.setBackground(cc);
        listView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.layout_background_bluetooth));

        bluetoothReceiver = new MyBluetoothBroadcastReceiver();

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        orientation = getResources().getConfiguration().orientation;

        if (MyDebug.LOG) {
            Log.i(TAG, "------------ onResume ---------- Current Orientation = " + orientation);
        }

        items.clear();
        listAdapter.notifyDataSetChanged();

        // Register for Bluetooth device found and discovery finished events
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(bluetoothReceiver, filter);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                items.add(new MyItem(device));
            }
            listAdapter.notifyDataSetChanged();
        }

        doDiscovery();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Check to see if the device was rotated while the dialog was displayed
        if (orientation != getResources().getConfiguration().orientation) {
            // We are pausing because of an orientation change
            dismiss();
        } else {
            // We're pausing for reasons other than a device rotation
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            settingsActivity.setbTScanFragDisplayed(false);
        }

        if (MyDebug.LOG) {
            Log.i(TAG, "------------ onPause ---------- Current Orientation = " + orientation);
        }

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        getActivity().unregisterReceiver(bluetoothReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        getActivity().setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    private class MyItem {
        private final BluetoothDevice device;

        MyItem(BluetoothDevice device) {
            this.device = device;
        }

    }

    private class MyDeviceAdapter extends BaseAdapter implements OnItemClickListener {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            MyItem item = items.get(position);
            boolean bonded = (item.device.getBondState() == BluetoothDevice.BOND_BONDED);
            int colorId = bonded ? android.R.color.black : android.R.color.darker_gray;
            int color = getResources().getColor(colorId);

            TextView text1 = convertView.findViewById(android.R.id.text1);
            text1.setText(item.device.getName());
            text1.setTextColor(color);

            TextView text2 = convertView.findViewById(android.R.id.text2);
            Locale myLocale = Resources.getSystem().getConfiguration().locale;
            String pairedStr = getString(bonded ? R.string.paired : R.string.not_paired);
            text2.setText(String.format(myLocale, "%s - %s", item.device.getAddress(), pairedStr));
            text2.setTextColor(color);

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            bluetoothAdapter.cancelDiscovery();

            if (listener != null) {
                listener.onBluetoothDeviceSelectedListener(items.get(position).device);
            }

            dismiss();
        }

    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private class MyBluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    items.add(new MyItem(device));
                    listAdapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    public interface OnBluetoothDeviceSelectedListener {

        void onBluetoothDeviceSelectedListener(BluetoothDevice device);
    }

}
