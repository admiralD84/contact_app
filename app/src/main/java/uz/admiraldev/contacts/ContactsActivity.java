package uz.admiraldev.contacts;

import static android.Manifest.permission.CALL_PHONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.contacts.adapters.ContactsAdapter;
import uz.admiraldev.contacts.databinding.ActivityContactsBinding;
import uz.admiraldev.contacts.models.Contact;

public class ContactsActivity extends AppCompatActivity
        implements ContactsAdapter.OnContactItemClickListener {
    List<Contact> contactsList = new ArrayList<>();
    private ContactsAdapter contactAdapter;
    MyShared<Contact> mySharedData;
    ActivityContactsBinding binding;
    private static final int REQUEST_CALL_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mySharedData = new MyShared<>(this, new Gson(), "contacts");

        getContacts();

        if (contactsList.size() != 0) {
            binding.ctEmptyList.setVisibility(View.GONE);
            binding.rvContactsList.setVisibility(View.VISIBLE);
            contactAdapter = new ContactsAdapter(this, contactsList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            binding.rvContactsList.setLayoutManager(linearLayoutManager);
            binding.rvContactsList.setAdapter(contactAdapter);
        } else {
            binding.rvContactsList.setVisibility(View.GONE);
            binding.ctEmptyList.setVisibility(View.VISIBLE);
        }
        initClickListener();

    }

    private void initClickListener() {
        binding.btnAddContact.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra("isEditContact", false);
            startActivity(intent);
            finish();
        });

    }

    public void popupMenu(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);
        try {
            Method method = popupMenu.getMenu().getClass()
                    .getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(popupMenu.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit_and_call) {
                editAndCallToContact(position);              //call
            } else if (item.getItemId() == R.id.action_delete) {
                deleteContact(position);                      //delete
            } else if (item.getItemId() == R.id.action_edit) {
                editContact(position);                        //edit
            } else if (item.getItemId() == R.id.action_send_message) {
                sendMessage(position);                        //send sms
            }
            return false;
        });
        popupMenu.show();
    }

    private void getContacts() {
        List<Contact> contactsFromShared =
                mySharedData.getList(
                        "contact",
                        new TypeToken<List<Contact>>() {
                        }.getType());

        if (contactsFromShared != null)
            contactsList = contactsFromShared;
    }

    private void deleteContact(int position) {
        contactsList.remove(position);
        contactAdapter.notifyItemRemoved(position);
        contactAdapter.notifyItemRangeChanged(position,
                contactsList.size() - position);
        if (contactsList.size() == 0) {
            binding.rvContactsList.setVisibility(View.GONE);
            binding.ctEmptyList.setVisibility(View.VISIBLE);
        }

        mySharedData.putList("contact", contactsList);
    }

    private void editAndCallToContact(int position) {
        String phoneNumber = contactsList.get(position).getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + phoneNumber));
        startActivity(callIntent);
    }

    public void callToContact(int position) {
        String phoneNumber = contactsList.get(position).getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
    }

    private void sendMessage(int position) {
        String phoneNumber = contactsList.get(position).getPhoneNumber();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", "");
        startActivity(smsIntent);
    }

    private void editContact(int position) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("isEditContact", true);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("isShowContact", true);
        startActivity(intent);
    }
}