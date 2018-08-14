package fusionsoftware.loop.dawaionline.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fusionsoftware.loop.dawaionline.R;
import fusionsoftware.loop.dawaionline.database.DbHelper;
import fusionsoftware.loop.dawaionline.fragments.AddNewAddressFragment;
import fusionsoftware.loop.dawaionline.fragments.LocationFragment;
import fusionsoftware.loop.dawaionline.fragments.YourOrderFragment;
import fusionsoftware.loop.dawaionline.framework.IAsyncWorkCompletedCallback;
import fusionsoftware.loop.dawaionline.framework.ServiceCaller;
import fusionsoftware.loop.dawaionline.model.Addresses;
import fusionsoftware.loop.dawaionline.model.Data;
import fusionsoftware.loop.dawaionline.utilities.Contants;
import fusionsoftware.loop.dawaionline.utilities.FontManager;
import fusionsoftware.loop.dawaionline.utilities.Utility;


/**
 * Created by user on 8/14/2017.
 */
public class UserAddressListAdapter extends RecyclerView.Adapter<UserAddressListAdapter.ViewHolder> {
    private List<Addresses> addresslist;
    private Context context;
    Typeface roboto_light, regular, materialdesignicons_font, medium;
    private LayoutInflater layoutInflater;
    private Boolean navigateFlag;
    private int localityId;
    private int StoreId;


    public UserAddressListAdapter(Context context, List<Addresses> addresslist, Boolean navigateFlag, int localityId,int StoreId) {
        this.context = context;
        this.addresslist = addresslist;
        this.navigateFlag = navigateFlag;
        this.localityId = localityId;
        this.StoreId=StoreId;
        layoutInflater = (LayoutInflater.from(context));
        roboto_light = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/roboto.light.ttf");
        this.medium = FontManager.getFontTypeface(context, "fonts/roboto.medium.ttf");
        this.regular = FontManager.getFontTypeface(context, "fonts/roboto.regular.ttf");
        materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_getall_addresss, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.completeaddress.setText(addresslist.get(position).getCompleteAddress() + "," + addresslist.get(position).getZipCode());
        holder.phonenumber.setText(addresslist.get(position).getPhoneNumber());
        holder.locality_name.setText(addresslist.get(position).getLocalityName());
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddNewAddressFragment fragment = AddNewAddressFragment.newInstance(addresslist.get(position).getAddressId(), true);//AddressId and true for edit address
                moveFragment(fragment);
            }
        });
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertForDeletetMessage(position);
            }
        });
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper dbHelper= new DbHelper(context);
                Data storeData=dbHelper.getStoreData(StoreId);
                if (navigateFlag) {
                        if (localityId != addresslist.get(position).getLocalityId()) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Change Location")
                                    .setCancelable(false)
                                    .setMessage("Your selected location is not match with address location.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            LocationFragment fragment = LocationFragment.newInstance("", true);
                                            moveFragment(fragment);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // user doesn't want to change the Location
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                        } else if (storeData != null) {
                            YourOrderFragment fragment = YourOrderFragment.newInstance(addresslist.get(position).getAddressId(), StoreId);
                            moveFragment(fragment);
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("Change Store")
                                    .setCancelable(false)
                                    .setMessage("selected store not comes under the selected location.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            LocationFragment fragment = LocationFragment.newInstance("", true);
                                            moveFragment(fragment);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // user doesn't want to change the Location
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }

                }
            }
        });

    }

    private void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


    //alert for delete
    public void alertForDeletetMessage(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        // alert.getWindow().getAttributes().windowAnimations = R.style.alertAnimation;
        View view = alert.getLayoutInflater().inflate(R.layout.delete_alert, null);
        TextView dec1 = (TextView) view.findViewById(R.id.dec1);
        dec1.setTypeface(regular);

        TextView ok = (TextView) view.findViewById(R.id.Ok);
        ok.setTypeface(regular);

        alert.setCustomTitle(view);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                deleteAddress(position);
            }
        });
        alert.show();
    }

    private void deleteAddress(int position) {

        if (Utility.isOnline(context)) {
            if (addresslist.get(position).getAddressId() != 0) {
                callDeleteAddressService(addresslist.get(position).getAddressId(), position);
            } else {

//                Intent intent = new Intent("data_action");
//                intent.putExtra("flag", "true");
//                context.sendBroadcast(intent);

            }
        } else {
            Toast.makeText(context, Contants.OFFLINE_MESSAGE, Toast.LENGTH_LONG).show();
        }
        notifyDataSetChanged();

    }


    //delete address
    private void callDeleteAddressService(int addressId, final int position) {
        DbHelper dbHelper= new DbHelper(context);
        Data data = dbHelper.getUserData();
        if (data!=null){
            final ServiceCaller serviceCaller = new ServiceCaller(context);
            serviceCaller.callDeleteAddressDataService(addressId, data.getLoginID(), new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String workName, boolean isComplete) {
                    if (isComplete) {
                        DbHelper dbHelper = new DbHelper(context);
                        dbHelper.deleteAddressData(addresslist.get(position).getAddressId());
                        addresslist.remove(position);
                        Intent intent=new Intent("data");
                        intent.putExtra("flag","true");
                        context.sendBroadcast(intent);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Address not Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return addresslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView completeaddress, Zipcode, phonenumber, tv_delete, tv_edit, locality_name;
        LinearLayout mainLayout;

        public ViewHolder(View view) {
            super(view);
            completeaddress = (TextView) view.findViewById(R.id.completeaddress);
            locality_name = (TextView) view.findViewById(R.id.locality_name);
            // Zipcode = (TextView) view.findViewById(R.id.Zipcode);
            phonenumber = (TextView) view.findViewById(R.id.phonenumber);
            tv_edit = (TextView) view.findViewById(R.id.tv_edit);
            tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
            completeaddress.setTypeface(medium);
            //   Zipcode.setTypeface(medium);
            tv_edit.setTypeface(medium);
            tv_delete.setTypeface(medium);
            phonenumber.setTypeface(regular);
            locality_name.setTypeface(regular);


        }
    }
}