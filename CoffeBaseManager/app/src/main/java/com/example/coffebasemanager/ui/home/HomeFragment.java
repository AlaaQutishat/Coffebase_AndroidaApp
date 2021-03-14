package com.example.coffebasemanager.ui.home;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.example.coffebasemanager.R;
import com.example.coffebasemanager.menulistitem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
public class HomeFragment extends Fragment {
   ListView l1;int childcount=0;
   String [] pricee;int []Caloriess;String [] itemname;
    String[] image; String cafenameee=" " ;
    private HomeViewModel homeViewModel;
    private FirebaseDatabase database ;
    private DatabaseReference myRef ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home ,container, false);
        SharedPreferences name = getActivity().getSharedPreferences("cafename", Context.MODE_PRIVATE);
        cafenameee=name.getString("name","datanotfound");
        final TextView cafename=root.findViewById(R.id.coffeename);
        cafename.setText(cafenameee);
        l1=(ListView)root.findViewById(R.id.menulist);
        filllist();
        return root;
    }

   public void filllist(){
       database = FirebaseDatabase.getInstance();
       myRef = database.getReference("Menu");

       myRef.child(cafenameee).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               childcount= (int) dataSnapshot.getChildrenCount();
               image = new String[childcount];
               pricee = new String[childcount];
               itemname = new String[childcount];
               Caloriess=new int[childcount];

               int i=0;
               for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                   if(dataSnapshot1.child("deactivate").getValue().toString().equals("0")){
                       String value = (String) dataSnapshot1.child("calories").getValue();
                       Caloriess[i]=Integer.parseInt(value);
                       String value2 = String.valueOf(dataSnapshot1.child("itemname").getValue());
                       itemname[i]=value2;
                       String value3 = String.valueOf(dataSnapshot1.child("price").getValue());
                       pricee[i]=value3;
                       String url = String.valueOf(dataSnapshot1.child("image").child("imageurl").getValue());
                       image[i]=url;
                       i++;
                   }}
               ArrayList<menulistitem> list= new ArrayList<menulistitem>();
               for(int j=0 ;j<i;j++){
                   list.add(new menulistitem(pricee[j],itemname[j],image[j],Caloriess[j]));

               }

               Listadapter adapter=new Listadapter(list);
               l1.setAdapter(adapter);

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       });
   }

    class Listadapter extends BaseAdapter{
   ArrayList <menulistitem>listitem =new <menulistitem>ArrayList();
        Listadapter(ArrayList <menulistitem> listadap) {
       this.listitem=listadap;

   }
        @Override
        public int getCount() {
            return listitem.size();

        }

        @Override
        public Object getItem(int position) {
            return listitem.get(position).itemname;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layot = getLayoutInflater();
            View v1 =layot.inflate(R.layout.menuitem,null);
            CircleImageView image=v1.findViewById(R.id.profile_image);
            TextView nameitem=v1.findViewById(R.id.name);
            TextView price=v1.findViewById(R.id.price);
            TextView Calories=v1.findViewById(R.id.Calories);
            nameitem.setText(listitem.get(position).itemname);
            Calories.setText(listitem.get(position).calories+"");
            price.setText(listitem.get(position).price);
            String u1 = listitem.get(position).image;
            Glide.with(getContext()).load(u1).into(image);



            return v1;
        }
    }
}