package com.example.musicartistsapp;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicartistsapp.databinding.AddArtistFragmentBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;

public class AddArtistFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "AddArtistFragment";
    private static final int REQUEST_CODE_VIDEO = 2;

    private AddArtistFragmentBinding addArtistFragmentBinding;

    private static final String IMAGE_URI = "image_uri";
    private static final String VIDEO_URI = "video_uri";

    private static final int REQUEST_CODE_IMAGE = 1;

    private String imageUri;
    private String videoUri;

    private StorageReference storageReference;

    interface AddArtistListener {
        void onAddArtist(ArtistModel artistModel);
    }

    private AddArtistListener addArtistListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addArtistFragmentBinding = AddArtistFragmentBinding.inflate(inflater, container, false);

        addArtistFragmentBinding.addArtistSave.setOnClickListener(this);
        addArtistFragmentBinding.addArtistCancel.setOnClickListener(this);
        addArtistFragmentBinding.buttonImage.setOnClickListener(this);
        addArtistFragmentBinding.buttonVideo.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference();

        return addArtistFragmentBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addArtistFragmentBinding = null;
        imageUri = null;
        videoUri = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AddArtistListener) {
            addArtistListener = (AddArtistListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void onSubmitClicked() {
        ArtistModel artist = new ArtistModel();
        try {
            artist.setName(addArtistFragmentBinding.artistNameEdit.getText().toString());
            artist.setDescription(addArtistFragmentBinding.artistDescriptionEdit.getText().toString());
            artist.setCountry((String) addArtistFragmentBinding.artistCountrySpinner.getSelectedItem());
            artist.setGenres(Arrays.asList((String) addArtistFragmentBinding.artistGenreSpinner.getSelectedItem()));

            artist.setImagePath(imageUri);
            artist.setVideoPath(videoUri);
        } catch (Exception e) {
            dismiss();
        }

        if (addArtistListener != null) {
            addArtistListener.onAddArtist(artist);
        }

        dismiss();
    }

    private void onCancelClicked() {
        dismiss();
    }

    private void onSelectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    private void onSelectVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageLocalUri = data.getData();
                    if (imageLocalUri != null) {
                        uploadImageFromFileUri(imageLocalUri);
                    }
                }
                break;
            case REQUEST_CODE_VIDEO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri videoLocalUri = data.getData();
                    if (videoLocalUri != null) {
                        uploadVideoFromFileUri(videoLocalUri);
                    }
                }
                break;
            default:
                //TODO: delete this debug output
                Toast.makeText(getActivity(), "Result code: -1", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageFromFileUri(Uri localUri) {
        final StorageReference ref = storageReference.child(localUri.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(localUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error when uploading image", Toast.LENGTH_LONG).show();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            //TODO: rewrite with ternary
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUri = downloadUri.toString();
                } else {
                    imageUri = null;
                }
            }
        });
    }

    private void uploadVideoFromFileUri(Uri localUri) {
        final StorageReference ref = storageReference.child(localUri.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(localUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error when uploading video", Toast.LENGTH_LONG).show();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            //TODO: rewrite with ternary
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    videoUri = downloadUri.toString();
                } else {
                    videoUri = null;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_artist_save:
                onSubmitClicked();
                break;
            case R.id.add_artist_cancel:
                onCancelClicked();
                break;
            case R.id.button_image:
                onSelectImage();
                break;
            case R.id.button_video:
                onSelectVideo();
                break;
        }
    }

//    @Override
//    public void updateBackground(int id) {
//        mBinding.newPizzaDialog.setBackgroundColor(getResources().getColor(id, null));
//    }
//
//    @Override
//    public void updateFontSize(int size) {
//
//    }
//
//    @Override
//    public void updateFontFamily(String family) {
//
//    }
}
