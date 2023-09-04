package uz.admiraldev.contacts.adapters;


import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import uz.admiraldev.contacts.ContactsActivity;
import uz.admiraldev.contacts.R;
import uz.admiraldev.contacts.models.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private static final int REQUEST_CODE_PERMISSION = 1;
    List<Contact> contacts;
    ContactsActivity activity;
    private OnContactItemClickListener clickListener;

    public ContactsAdapter(
            ContactsActivity activity,
            List<Contact> contacts, OnContactItemClickListener clickListener) {
        this.activity = activity;
        this.contacts = contacts;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Contact contact = contacts.get(position);
        String tempNumber = contact.getPhoneNumber();
        holder.contactPhone.setText(tempNumber);
        holder.contactName.setText(contact.getFirstName());
        if (contact.getContactPhotoUri() != null) {
            Uri tempUri = Uri.parse(contact.getContactPhotoUri());
            if (ContextCompat.checkSelfPermission(this.activity, READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.activity, new String[]{READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION);
            } else {
                try {
                    holder.contactImageView.setImageURI(tempUri);
                } catch (Exception ignored) {
                }
            }
        } else
            holder.contactImageView.setImageResource(R.drawable.default_contact_image);
        holder.icCall.setOnClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                clickListener.callToContact(position);
                //this.activity.callToContact(position);
            }
        });
        holder.icMenu.setOnClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                // clickListener.onContactClicked(position);
                clickListener.popupMenu(position, view);
            }
        });
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView contactImageView, icCall, icMenu;
        TextView contactName, contactPhone;
        RecyclerView rvContacts;
        LinearLayout emptyListImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.tv_contact_name);
            contactImageView = itemView.findViewById(R.id.iv_contact_avatar);
            contactPhone = itemView.findViewById(R.id.tv_contact_phone_number);
            icCall = itemView.findViewById(R.id.ic_call);
            icMenu = itemView.findViewById(R.id.ic_menu);
            rvContacts = itemView.findViewById(R.id.rv_contacts_list);
            emptyListImage = itemView.findViewById(R.id.ct_empty_list);
        }
    }


    public interface OnContactItemClickListener {

        void onItemClick(int position);

        void callToContact(int position);

        void popupMenu(int position, View view);
    }

}
