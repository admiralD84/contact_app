package uz.admiraldev.contacts;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import uz.admiraldev.contacts.databinding.ActivityAddContactBinding;
import uz.admiraldev.contacts.models.Contact;

public class AddContactActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 1;
    ActivityAddContactBinding binding;
    MyShared<Contact> mySharedData;
    Uri selectedImageUri;
    String selectedImageString;
    List<Contact> contactsList;
    boolean isEditContact;
    boolean isShowContact;
    int contactPosition;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mySharedData = new MyShared<>(this, new Gson(), "contacts");
        contactsList = mySharedData.getList("contact", new TypeToken<List<Contact>>() {
        }.getType());
        initClicks();

        isEditContact = getIntent().getBooleanExtra("isEditContact", false);
        isShowContact = getIntent().getBooleanExtra("isShowContact", false);
        contactPosition = getIntent().getIntExtra("position", 0);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        binding.ivContactAvatar.setImageURI(selectedImageUri);
                        selectedImageString = selectedImageUri.toString();
                    }
                });
        if (isEditContact || isShowContact) {
            Contact contact = contactsList.get(contactPosition);
            binding.etFirstName.setText(contact.getFirstName());
            binding.etLastName.setText(contact.getLastName());
            binding.etOrganization.setText(contact.getOrganization());
            binding.etPhoneNumber.setText(contact.getPhoneNumber());
            binding.etEmailAddress.setText(contact.getEmail());
            binding.btnSave.setTextAppearance(this, R.style.enabledBtnTextStyle);
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION);
            } else {
                if (contact.getContactPhotoUri() != null) {
                    Uri tempUri = Uri.parse(contact.getContactPhotoUri());
                    binding.ivContactAvatar.setImageURI(tempUri);
                } else
                    binding.ivContactAvatar.setImageResource(R.drawable.default_contact_image);
            }
            if (isShowContact) {
                binding.etPhoneNumber.setFocusable(false);
                binding.etPhoneNumber.setClickable(false);
                binding.etFirstName.setFocusable(false);
                binding.etFirstName.setClickable(false);
                binding.etLastName.setFocusable(false);
                binding.etLastName.setClickable(false);
                binding.etOrganization.setFocusable(false);
                binding.etOrganization.setClickable(false);
                binding.etEmailAddress.setFocusable(false);
                binding.etEmailAddress.setClickable(false);
                binding.btnSave.setVisibility(View.GONE);
                binding.btnAddPhoto.setVisibility(View.GONE);
                binding.btnCancel.setText("Orqaga");
                if(binding.etLastName.getText().toString().isEmpty())
                    binding.etLastName.setVisibility(View.GONE);
                if(binding.etOrganization.getText().toString().isEmpty())
                    binding.etOrganization.setVisibility(View.GONE);
                if(binding.etEmailAddress.getText().toString().isEmpty())
                    binding.etEmailAddress.setVisibility(View.GONE);
                if(contact.isMale()){
                    binding.rbFemale.setVisibility(View.GONE);
                    binding.rbMale.setClickable(false);
                    binding.rbMale.setEnabled(false);
                }else {
                    binding.rbMale.setVisibility(View.GONE);
                    binding.rbMale.setClickable(false);
                    binding.rbMale.setEnabled(false);
                }
            }
        } else {
            binding.btnSave.setEnabled(false);
            binding.btnSave.setTextAppearance(this, R.style.disabledBtnTextStyle);
        }
    }

    private void initClicks() {
        binding.btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            finish();
        });
        binding.etPhoneNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                String currentText = binding.etPhoneNumber.getText().toString();
                if (!currentText.startsWith("+998")) {
                    binding.etPhoneNumber.setText("+998");
                    binding.etPhoneNumber.setSelection(binding.etPhoneNumber.getText().length());
                }
            }
            binding.btnSave.setEnabled(true);
            binding.btnSave.setTextAppearance(this, R.style.enabledBtnTextStyle);
            binding.btnSave.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save, 0, 0, 0);
        });
        binding.btnSave.setOnClickListener(view -> {
            if (isEditContact)
                contactsList.remove(contactPosition);
            String contactFirstName = binding.etFirstName.getText().toString();
            String contactPhoneNumber = binding.etPhoneNumber.getText().toString();
            if (!contactFirstName.isEmpty() && !contactPhoneNumber.equals("+998")) {
                if (contactsList == null) {
                    contactsList = new ArrayList<>();
                }
                Contact newContact = new Contact(
                        contactFirstName,
                        binding.etLastName.getText().toString(),
                        binding.etEmailAddress.getText().toString(),
                        binding.etOrganization.getText().toString(),
                        contactPhoneNumber,
                        selectedImageString, true);

                contactsList.add(newContact);

                mySharedData.putList("contact", contactsList);

                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ma'lumot kiritilmadi", Toast.LENGTH_SHORT).show();
                if (binding.etFirstName.getText().toString().isEmpty())
                    binding.etFirstName.requestFocus();
                if (binding.etPhoneNumber.getText().toString().equals("+998")) {
                    binding.etPhoneNumber.requestFocus();                       // focus o'rnatish
                    binding.etPhoneNumber.setSelection(binding.etPhoneNumber.getText().length());
                }
            }
        });
        binding.btnAddPhoto.setOnClickListener(view -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

}