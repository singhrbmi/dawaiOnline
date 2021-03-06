package fusionsoftware.loop.dawaionline.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fusionsoftware.loop.dawaionline.R;
import fusionsoftware.loop.dawaionline.database.DbHelper;
import fusionsoftware.loop.dawaionline.fragments.MyBasketFragment;
import fusionsoftware.loop.dawaionline.fragments.UserAddressListFragment;
import fusionsoftware.loop.dawaionline.model.Data;
import fusionsoftware.loop.dawaionline.model.MyBasket;
import fusionsoftware.loop.dawaionline.utilities.FontManager;
import fusionsoftware.loop.dawaionline.utilities.Utility;


/**
 * Created by user on 8/14/2017.
 */
public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {
    private List<MyBasket> basketdata;
    private Context context;
    private Typeface materialDesignIcons, medium, regular, bold;
    DbHelper dbHelper;
    MyBasketFragment myBasketFragment;

    public BasketAdapter(Context context, List<MyBasket> basketdata, MyBasketFragment myBasketFragment) {
        this.context = context;
        this.basketdata = basketdata;
        this.myBasketFragment = myBasketFragment;
        this.medium = FontManager.getFontTypeface(context, "fonts/roboto.medium.ttf");
        this.regular = FontManager.getFontTypeface(context, "fonts/roboto.regular.ttf");
        this.bold = FontManager.getFontTypeface(context, "fonts/roboto.bold.ttf");
        this.materialDesignIcons = FontManager.getFontTypefaceMaterialDesignIcons(context, "fonts/materialdesignicons-webfont.otf");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_basket_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        dbHelper = new DbHelper(context);
        viewHolder.categoryName.setText(basketdata.get(i).getCategoryName());
        List<MyBasket> orderList = dbHelper.GetAllBasketOrderDataBasedOnCategoryId(basketdata.get(i).getCategoryId());
        if (orderList != null && orderList.size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            viewHolder.recycleView_inner.setLayoutManager(layoutManager);
            BasketInnerAdapter adapter = new BasketInnerAdapter(context, orderList, basketdata, i, basketdata.get(i).getCategoryId(), myBasketFragment);
            viewHolder.recycleView_inner.setAdapter(adapter);
        }


//        viewHolder.checkoutLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UserAddressListFragment fragment = UserAddressListFragment.newInstance(true, 0);
//                moveFragment(fragment);
//            }
//        });
    }

    private void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return basketdata.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recycleView_inner;
        TextView categoryName;

        public ViewHolder(View view) {
            super(view);
            recycleView_inner = (RecyclerView) view.findViewById(R.id.recycleView_inner);
            //   edit = (TextView) view.findViewById(R.id.edit);
            categoryName = (TextView) view.findViewById(R.id.categoryName);
            categoryName.setTypeface(medium);
        }
    }

}
