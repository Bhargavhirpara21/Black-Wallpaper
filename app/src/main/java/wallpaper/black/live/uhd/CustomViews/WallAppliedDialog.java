package wallpaper.black.live.uhd.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import wallpaper.black.live.uhd.R;

public class WallAppliedDialog {

    private String title, message, positiveBtnText;
    @ColorRes
    private int pBtnColor;
    @ColorRes
    private int titleTxtColor, desTxtColor;
    private Context context;
    private WallAppliedDialogListener pListener, nListener;
    private Dialog.OnCancelListener cancelListener;
    private boolean cancel;
    int gifImageResource;


    private WallAppliedDialog(Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.context = builder.context;
        this.pListener = builder.pListener;
        this.nListener = builder.nListener;
        this.titleTxtColor = builder.titleTxtColor;
        this.desTxtColor = builder.desTxtColor;
        this.pBtnColor = builder.pBtnColor;
        this.positiveBtnText = builder.positiveBtnText;
        this.gifImageResource = builder.gifImageResource;
        this.cancel = builder.cancel;
        this.cancelListener = builder.cancelListener;
    }


    public static class Builder {
        private String title, message, positiveBtnText;
        @ColorRes
        private int pBtnColor;
        private int titleTxtColor, desTxtColor;
        private Context context;
        private WallAppliedDialogListener pListener, nListener;
        private boolean cancel;
        int gifImageResource;
        private Dialog.OnCancelListener cancelListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(@StringRes int title) {
            return setTitle(context.getString(title));
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(@StringRes int message) {
            return setMessage(context.getString(message));
        }

        public Builder setTitleTextColor(@ColorRes int titleTxtColor) {
            this.titleTxtColor = titleTxtColor;
            return this;
        }

        public Builder setDescriptionTextColor(@ColorRes int desTxtColor) {
            this.desTxtColor = desTxtColor;
            return this;
        }

        public Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public Builder setPositiveBtnText(@StringRes int positiveBtnText) {
            return setPositiveBtnText(context.getString(positiveBtnText));
        }

        public Builder setPositiveBtnBackground(@ColorRes int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        //set Positive listener
        public Builder OnPositiveClicked(WallAppliedDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        //set Negative listener
        public Builder OnNegativeClicked(WallAppliedDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder setOnCancelListener(Dialog.OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setGifResource(int gifImageResource) {
            this.gifImageResource = gifImageResource;
            return this;
        }

        public WallAppliedDialog build() {
            TextView message1, title1;
            LinearLayout pBtn;
            ImageView gifImageView;

            final Dialog dialog;
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(cancel);
            dialog.setContentView(R.layout.fancygifdialog);


            //getting resources
            title1 = dialog.findViewById(R.id.title);
            message1 = dialog.findViewById(R.id.message);
            pBtn = dialog.findViewById(R.id.positiveBtn);
            gifImageView = dialog.findViewById(R.id.gifImageView);
            Glide.with(context)
                    .load(gifImageResource).transition(DrawableTransitionOptions.withCrossFade())
                    .apply(new RequestOptions())
                    .into(gifImageView);

            title1.setText(title);
            message1.setText(message);

            title1.setTextColor(ContextCompat.getColor(context, titleTxtColor));
            message1.setTextColor(ContextCompat.getColor(context, desTxtColor));

//            GradientDrawable pbgShape = (GradientDrawable) pBtn.getBackground();
//            pbgShape.setColor(ContextCompat.getColor(context, pBtnColor));
            if (pListener != null) {
                pBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pListener.OnClick();
                        dialog.dismiss();
                    }
                });
            } else {
                pBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }

                });
            }

            if (cancelListener != null)
                dialog.setOnCancelListener(cancelListener);


            dialog.show();

            return new WallAppliedDialog(this);
        }
    }

}