// Generated by view binder compiler. Do not edit!
package com.shaikshavali.taskthree.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.shaikshavali.taskthree.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMapBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton btnUpdateLocation;

  @NonNull
  public final AppCompatEditText etAddress;

  @NonNull
  public final AppCompatEditText etDate;

  @NonNull
  public final TextView imgNav;

  @NonNull
  public final Toolbar toolbarMapScreen;

  @NonNull
  public final TextView tvAddress;

  @NonNull
  public final TextView tvDateTime;

  @NonNull
  public final TextView tvGalleryTop;

  private ActivityMapBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppCompatButton btnUpdateLocation, @NonNull AppCompatEditText etAddress,
      @NonNull AppCompatEditText etDate, @NonNull TextView imgNav,
      @NonNull Toolbar toolbarMapScreen, @NonNull TextView tvAddress, @NonNull TextView tvDateTime,
      @NonNull TextView tvGalleryTop) {
    this.rootView = rootView;
    this.btnUpdateLocation = btnUpdateLocation;
    this.etAddress = etAddress;
    this.etDate = etDate;
    this.imgNav = imgNav;
    this.toolbarMapScreen = toolbarMapScreen;
    this.tvAddress = tvAddress;
    this.tvDateTime = tvDateTime;
    this.tvGalleryTop = tvGalleryTop;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_map, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMapBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_update_location;
      AppCompatButton btnUpdateLocation = ViewBindings.findChildViewById(rootView, id);
      if (btnUpdateLocation == null) {
        break missingId;
      }

      id = R.id.et_address;
      AppCompatEditText etAddress = ViewBindings.findChildViewById(rootView, id);
      if (etAddress == null) {
        break missingId;
      }

      id = R.id.et_date;
      AppCompatEditText etDate = ViewBindings.findChildViewById(rootView, id);
      if (etDate == null) {
        break missingId;
      }

      id = R.id.img_nav;
      TextView imgNav = ViewBindings.findChildViewById(rootView, id);
      if (imgNav == null) {
        break missingId;
      }

      id = R.id.toolbar_map_screen;
      Toolbar toolbarMapScreen = ViewBindings.findChildViewById(rootView, id);
      if (toolbarMapScreen == null) {
        break missingId;
      }

      id = R.id.tv_address;
      TextView tvAddress = ViewBindings.findChildViewById(rootView, id);
      if (tvAddress == null) {
        break missingId;
      }

      id = R.id.tv_date_time;
      TextView tvDateTime = ViewBindings.findChildViewById(rootView, id);
      if (tvDateTime == null) {
        break missingId;
      }

      id = R.id.tv_gallery_top;
      TextView tvGalleryTop = ViewBindings.findChildViewById(rootView, id);
      if (tvGalleryTop == null) {
        break missingId;
      }

      return new ActivityMapBinding((ConstraintLayout) rootView, btnUpdateLocation, etAddress,
          etDate, imgNav, toolbarMapScreen, tvAddress, tvDateTime, tvGalleryTop);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
