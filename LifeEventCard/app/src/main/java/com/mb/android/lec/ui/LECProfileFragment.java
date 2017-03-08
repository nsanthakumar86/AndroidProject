package com.mb.android.lec.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECUser;
import com.mb.android.lec.db.UserSession;
import com.mb.android.lec.util.ImageUtil;
import com.mb.android.lec.util.LECSharedPreferenceManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LECProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LECProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LECProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CAMERA_REQUEST = 888;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView userImage;
    private OnFragmentInteractionListener mListener;
    private int imageID;
    LECUser lecUser;
    Context context;

    public LECProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LECProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LECProfileFragment newInstance(String param1, String param2) {
        LECProfileFragment fragment = new LECProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        context = getActivity();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        String emailId = LECSharedPreferenceManager.getLoggedinUserMailId(getActivity());

        lecUser = UserSession.getInstance().getActiveUser();
        View view = inflater.inflate(R.layout.fragment_lec_profile, container, false);

        ((TextView)view.findViewById(R.id.user_name)).setText(lecUser.getFullName());
        ((TextView)view.findViewById(R.id.user_email)).setText(lecUser.getUserMailId());
        ((TextView)view.findViewById(R.id.user_phone)).setText(lecUser.getPhoneNumber());

        userImage = (ImageView) view.findViewById(R.id.user_photo);
        if(!TextUtils.isEmpty(lecUser.getProfileImg())){
            ImageUtil.setCircularImage(getBitmap(lecUser.getProfileImg()), userImage);
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionOption();
            }
        });

        return view;
    }

    private Bitmap getBitmap(String profileImg) {
        Bitmap bitmap = BitmapFactory.decodeFile(profileImg);
        return bitmap;
    }

    private void showSelectionOption(){
        CharSequence colors[] = new CharSequence[] {"Gallery", "Camera"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(null);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which){
                    case 0:
                        Intent intent = new Intent(getActivity(), LECMultiPhotoSelectActivity.class);
                        startActivityForResult(intent, LECMultiPhotoSelectActivity.GALLERY_REQUEST_CODE);
                        break;
                    case 1:

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                }
            }
        });
        builder.show();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onProfileImageChanged();
    }

    public void setListener(OnFragmentInteractionListener listeer){
        mListener = listeer;
    }

    @Override
     public void onActivityResult(int requestCode,int resultCode,Intent data){

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo1 = (Bitmap) data.getExtras().get("data");
            ImageUtil.setCircularImage(photo1, userImage);
//            BitmapDrawable photo = new BitmapDrawable(getResources(), photo1);
            Uri uri = data.getData();
            String photoPath = getRealPathFromURI(uri);
            LECQueryManager.updateProfileImage(lecUser.getId(), photoPath);
            if(mListener != null){
                mListener.onProfileImageChanged();
            }


        }else if(requestCode == LECMultiPhotoSelectActivity.GALLERY_REQUEST_CODE /*&& resultCode == LECMultiPhotoSelectActivity.GALLERY_RESULT_CODE*/){
            ArrayList<String> images = data.getExtras().getStringArrayList(LECMultiPhotoSelectActivity.SELECTED_IMAGES);
            if(images == null || images.size()==0) return;
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            ImageUtil.setCircularImage(bitmap, userImage);
            LECQueryManager.updateProfileImage(lecUser.getId(), images.get(0));

            if(mListener != null){
                mListener.onProfileImageChanged();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private String getRealPathFromURI(Uri contentUri) {
        CursorLoader loader = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
