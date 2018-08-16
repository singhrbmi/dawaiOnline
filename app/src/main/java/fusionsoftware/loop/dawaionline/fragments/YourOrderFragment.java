package fusionsoftware.loop.dawaionline.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fusionsoftware.loop.dawaionline.R;
import fusionsoftware.loop.dawaionline.activity.DashboardActivity;
import fusionsoftware.loop.dawaionline.adapter.YourOrderAdpater;
import fusionsoftware.loop.dawaionline.balltrianglecustomprogress.BallTriangleDialog;
import fusionsoftware.loop.dawaionline.database.DbHelper;
import fusionsoftware.loop.dawaionline.framework.IAsyncWorkCompletedCallback;
import fusionsoftware.loop.dawaionline.framework.ServiceCaller;
import fusionsoftware.loop.dawaionline.model.Addresses;
import fusionsoftware.loop.dawaionline.model.ContentData;
import fusionsoftware.loop.dawaionline.model.CreateOrderDetails;
import fusionsoftware.loop.dawaionline.model.Data;
import fusionsoftware.loop.dawaionline.model.MyBasket;
import fusionsoftware.loop.dawaionline.utilities.CompatibilityUtility;
import fusionsoftware.loop.dawaionline.utilities.Contants;
import fusionsoftware.loop.dawaionline.utilities.FontManager;
import fusionsoftware.loop.dawaionline.utilities.Utility;

/**
 * Created by LALIT on 8/14/2017.
 */
public class YourOrderFragment extends Fragment implements View.OnClickListener {
    private int addressId;
    private int StoreId;

    public static YourOrderFragment newInstance(int addressId, int StoreId) {
        YourOrderFragment fragment = new YourOrderFragment();
        Bundle args = new Bundle();
        args.putInt("AddressId", addressId);
        args.putInt("StoreId", StoreId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressId = getArguments().getInt("AddressId");
            StoreId = getArguments().getInt("StoreId");
        }
    }


    YourOrderAdpater adapter;
    EditText edt_promoCode;
    View view;
    TextView arrow_icon, tv_continue, your_order, tv_editOrder, tv_off, tv_promoCode, tv_apply, tv_specialDiscount_charges, tv_specialDiscount,
            rupee_icon, icon_rupees, tv_rupees_icon, rupees_icon, tv_cancel, tv_done,
            tv_deliverTo, tv_edit_Icon, tv_deliveryAddress, quantity, dish_name, price, action, total, total_amount, shipping, shipping_charges, grand_amount, grand_total, tv_payOnline;
    private Context context;
    private int loginID;
    private float promoCodeDiscount;
    private LinearLayout layout_promoCode, tv_continueLayout, layout_done;
    private ArrayList<CreateOrderDetails> orderDetailsesList;
    private double totalPrice = 0;
    private int storeId;
    int opTime, endTime;
    private Boolean editFlag = false;
    private double SubTotalPrice = 0;
    //private double totalDiscountPrice = 0;
    private int promoCodeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (CompatibilityUtility.isTablet(context)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        view = inflater.inflate(R.layout.fragment_add_yourorder, container, false);
        init();
        return view;
    }


    //set icons..........
    private void setIcons() {
        Typeface italic = FontManager.getFontTypeface(context, "fonts/roboto.italic.ttf");
        Typeface medium = FontManager.getFontTypeface(context, "fonts/roboto.medium.ttf");
        Typeface regular = FontManager.getFontTypeface(context, "fonts/roboto.regular.ttf");
        Typeface bold = FontManager.getFontTypeface(context, "fonts/roboto.bold.ttf");
        Typeface materialdesignicons_font = FontManager.getFontTypefaceMaterialDesignIcons(getActivity().getApplicationContext(), "fonts/materialdesignicons-webfont.otf");
        arrow_icon.setTypeface(materialdesignicons_font);
        arrow_icon.setText(Html.fromHtml("&#xf054;"));
        rupee_icon.setTypeface(materialdesignicons_font);
        rupee_icon.setText(Html.fromHtml("&#xf1af;"));
        icon_rupees.setTypeface(materialdesignicons_font);
        icon_rupees.setText(Html.fromHtml("&#xf1af;"));
        rupees_icon.setTypeface(materialdesignicons_font);
        rupees_icon.setText(Html.fromHtml("&#xf1af;"));

        tv_rupees_icon.setTypeface(materialdesignicons_font);
        tv_rupees_icon.setText(Html.fromHtml("&#xf1af;"));
        tv_edit_Icon.setTypeface(materialdesignicons_font);
        tv_edit_Icon.setText(Html.fromHtml("&#xf3eb;"));
        tv_cancel.setTypeface(materialdesignicons_font);
        tv_cancel.setText(Html.fromHtml("&#xf156;"));
        tv_done.setTypeface(materialdesignicons_font);
        tv_done.setText(Html.fromHtml("&#xf12c;"));
        tv_deliverTo.setTypeface(medium);
        tv_deliveryAddress.setTypeface(regular);
        your_order.setTypeface(bold);
        //  tv_editOrder.setTypeface(medium);
        quantity.setTypeface(regular);
        dish_name.setTypeface(regular);
        action.setTypeface(regular);
        price.setTypeface(regular);
        total.setTypeface(regular);
        total_amount.setTypeface(regular);
        shipping.setTypeface(regular);
        tv_specialDiscount.setTypeface(regular);
        shipping_charges.setTypeface(regular);
        tv_specialDiscount_charges.setTypeface(regular);
        grand_amount.setTypeface(medium);
        grand_total.setTypeface(medium);
        edt_promoCode.setTypeface(regular);
        tv_promoCode.setTypeface(regular);
        tv_apply.setTypeface(medium);
        //  tv_off.setTypeface(medium);

        //  tv_off.setTypeface(italic);
    }

    //initlization..............
    private void init() {
        DashboardActivity rootActivity = (DashboardActivity) getActivity();
        rootActivity.setScreencart(false);
        rootActivity.setScreenSave(false);
        rootActivity.setScreenCartDot(false);
        edt_promoCode = (EditText) view.findViewById(R.id.edt_promoCode);
        // tv_off = (TextView) view.findViewById(R.id.tv_off);
        tv_promoCode = (TextView) view.findViewById(R.id.tv_promoCode);
        tv_apply = (TextView) view.findViewById(R.id.tv_apply);
        tv_apply.setOnClickListener(this);
        tv_continue = (TextView) view.findViewById(R.id.tv_continue);
        arrow_icon = (TextView) view.findViewById(R.id.arrow_icon);
        your_order = (TextView) view.findViewById(R.id.your_order);
        //tv_editOrder = (TextView) view.findViewById(R.id.tv_editOrder);
        rupee_icon = (TextView) view.findViewById(R.id.rupee_icon);
        icon_rupees = (TextView) view.findViewById(R.id.icon_rupees);
        rupees_icon = (TextView) view.findViewById(R.id.rupees_icon);
        quantity = (TextView) view.findViewById(R.id.quantity);
        dish_name = (TextView) view.findViewById(R.id.dish_name);
        action = (TextView) view.findViewById(R.id.action);
        price = (TextView) view.findViewById(R.id.price);
        total = (TextView) view.findViewById(R.id.total);
        total_amount = (TextView) view.findViewById(R.id.total_amount);
        shipping = (TextView) view.findViewById(R.id.shipping);
        tv_specialDiscount = (TextView) view.findViewById(R.id.tv_specialDiscount);
        shipping_charges = (TextView) view.findViewById(R.id.shipping_charges);
        tv_specialDiscount_charges = (TextView) view.findViewById(R.id.tv_specialDiscount_charges);
        grand_amount = (TextView) view.findViewById(R.id.grant_amount);
        grand_total = (TextView) view.findViewById(R.id.grand_total);
        tv_payOnline = (TextView) view.findViewById(R.id.tv_continue);
        tv_rupees_icon = (TextView) view.findViewById(R.id.tv_rupees_icon);
        tv_deliverTo = (TextView) view.findViewById(R.id.tv_deliverTo);
        tv_deliveryAddress = (TextView) view.findViewById(R.id.tv_deliveryAddress);
        tv_edit_Icon = (TextView) view.findViewById(R.id.tv_edit_icon);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_done = (TextView) view.findViewById(R.id.tv_done);

        layout_promoCode = (LinearLayout) view.findViewById(R.id.layout_promoCode);
        layout_done = (LinearLayout) view.findViewById(R.id.layout_done);
        tv_continueLayout = (LinearLayout) view.findViewById(R.id.tv_continueLayout);
        tv_continueLayout.setOnClickListener(this);
        tv_edit_Icon.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        //tv_editOrder.setOnClickListener(this);
        layout_done.setOnClickListener(this);
        setIcons();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listrecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        getStoreIdFromPreferences();
        orderDetailsesList = new ArrayList<CreateOrderDetails>();
        DbHelper dbHelper = new DbHelper(context);
        Data userData = dbHelper.getUserData();
        Addresses addresses = dbHelper.getAllAddressesData(addressId);
        loginID = userData.getLoginID();
        List<MyBasket> orderList = dbHelper.GetAllBasketOrderDataBasedOnCategoryId(StoreId);
        if (orderList != null && orderList.size() > 0) {
            adapter = new YourOrderAdpater(context, orderList, storeId);
            recyclerView.setAdapter(adapter);
            orderDetails(orderList);
        }
        if (addresses != null) {
            tv_deliveryAddress.setText(addresses.getCompleteAddress() + "," + addresses.getZipCode() + "," + addresses.getPhoneNumber());
        }

    }

    //get selected store id
    private void getStoreIdFromPreferences() {
        SharedPreferences prefs = context.getSharedPreferences("StoreIdPreferences", Context.MODE_PRIVATE);
        if (prefs != null) {
            storeId = prefs.getInt("StoreId", 0);
        }
    }

    private void orderDetails(List<MyBasket> orderList) {
        for (MyBasket order : orderList) {
            CreateOrderDetails orderDetails = new CreateOrderDetails();
            orderDetails.setProductId(order.getProductId());
            orderDetails.setQuantity(order.getQuantity());
            orderDetailsesList.add(orderDetails);
        }
    }
    //.....................calculate total price with all discount.................//
//    private double calculateTotalQuantityPrice(float Quantity, float itemPrice, float Discount) {
//        double totalPrice = 0;
//        double priceAfterDiscount = 0;
//        double totalPriceAfterDiscount = 0;
//        double totalPriceWithQuantity = Quantity * itemPrice;
//        if (Discount != 0) {
//            priceAfterDiscount = (totalPriceWithQuantity / 100.0f) * Discount;// discount in total Quantity with price
//            totalPriceAfterDiscount = totalPriceWithQuantity - priceAfterDiscount;//get total price after discount amount
//            totalDiscountPrice = totalDiscountPrice + priceAfterDiscount;//get total discount amount
//            totalPrice = totalPriceAfterDiscount;//total price after discount
//        } else {
//            totalPrice = totalPriceWithQuantity;//total price with Quantity
//        }
//        return totalPrice;
//    }

//
//    private void calculateTotalPrice(List<MyBasket> orderList) {
//
//        for (MyBasket order : orderList) {
//            //  double totalProductPrice = calculateTotalQuantityPrice(order.getQuantity(), order.getPrice(), order.getDiscount());
//            //    totalPrice = totalPrice + totalProductPrice;
////            SubTotalPrice = SubTotalPrice + order.getPrice();
//            // SubTotalPrice = SubTotalPrice + order.getQuantity() * order.getPrice();
//            CreateOrderDetails orderDetails = new CreateOrderDetails();
//            orderDetails.setProductId(order.getProductId());
//            orderDetails.setQuantity(order.getQuantity());
////            orderDetails.setTotalPrice(totalProductPrice);
//            orderDetailsesList.add(orderDetails);
//        }
//        if (promoCodeDiscount != 0) {
//            totalDiscountPrice = totalDiscountPrice + promoCodeDiscount;
//            tv_specialDiscount_charges.setText(String.valueOf(totalDiscountPrice));//show discount
////            totalPrice = calculatePromoCodeDiscount(totalPrice);
//        }
////        total_amount.setText(String.valueOf(SubTotalPrice));
////        grand_amount.setText(String.valueOf(totalPrice));
////        tv_specialDiscount_charges.setText(String.valueOf(totalDiscountPrice));
//    }

    //calculate promo code discount on total amount
//    private double calculatePromoCodeDiscount(double totalPrice) {
////        double priceAfterPromoCodeDiscount = 0;
////        //priceAfterPromoCodeDiscount = (totalPrice / 100.0f) * promoCodeDiscount;// promo code discount in total priceAfterDiscount
////        priceAfterPromoCodeDiscount = totalPrice - promoCodeDiscount;
////        return priceAfterPromoCodeDiscount;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_continueLayout:
                createNewOrder();
                break;
            case R.id.tv_cancel:
                edt_promoCode.setText("");
                tv_apply.setVisibility(View.VISIBLE);
                tv_cancel.setVisibility(View.GONE);
                break;
            case R.id.layout_done:
                break;
        }
    }


    //create new Order

    public void createNewOrder() {
        if (Utility.isOnline(context)) {
            final BallTriangleDialog dotDialog = new BallTriangleDialog(context);
            dotDialog.show();
            final DbHelper dbHelper = new DbHelper(context);
            Data data = dbHelper.getUserData();
            int loginId = data.getLoginID();
            ServiceCaller serviceCaller = new ServiceCaller(context);
            serviceCaller.createOrderService(storeId, addressId, loginId, promoCodeId, orderDetailsesList, new IAsyncWorkCompletedCallback() {
                @Override
                public void onDone(String result, boolean isComplete) {
                    if (isComplete) {
                        if (result != null) {
                            OrderConfirmFragment fragment = OrderConfirmFragment.newInstance(addressId, result);
                            moveFragmentWithTag(fragment, "OrderPlacedFragment");
                        } else {
                            Utility.alertForErrorMessage("Order not Placed Successfully", context);
                        }
                    } else {
                        Utility.alertForErrorMessage("Order not Placed Successfully", context);
                    }
                    if (dotDialog.isShowing()) {
                        dotDialog.dismiss();
                    }
                }
            });


        } else {
            Utility.alertForErrorMessage(Contants.OFFLINE_MESSAGE, context);
        }
    }

    private void moveFragmentWithTag(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    //check edit or not
    private void checkEditOrNot() {
        if (editFlag) {
            action.setVisibility(View.VISIBLE);
            your_order.setText("Edit Your Order");
            tv_done.setVisibility(View.VISIBLE);
            tv_editOrder.setText("DONE");
            tv_edit_Icon.setVisibility(View.VISIBLE);
            layout_promoCode.setVisibility(View.GONE);
            adapter.setEditDeleteRequired(true);
            editFlag = false;
        } else {
            layout_promoCode.setVisibility(View.VISIBLE);
            action.setVisibility(View.GONE);
            your_order.setText("Your Order");
            tv_editOrder.setText("EDIT ORDER");
            tv_done.setVisibility(View.GONE);
            tv_edit_Icon.setVisibility(View.INVISIBLE);
            if (adapter != null) {
                adapter.setEditDeleteRequired(false);
            }
            editFlag = true;
        }
        adapter.notifyDataSetChanged();
    }

    private void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
