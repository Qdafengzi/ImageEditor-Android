package com.xinlan.imageeditlibrary.editimage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.databinding.ActivityImageEditBinding;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.view.AddImageGroupView;
import com.xinlan.imageeditlibrary.editimage.view.AddTextItemView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;
import com.xinlan.imageeditlibrary.editimage.widget.RedoUndoController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * 图片编辑 主页面
 *
 * @author panyi
 * <p>
 * 包含 1.贴图 2.滤镜 3.剪裁 4.底图旋转 功能
 * add new modules
 */
public class EditImageFragment extends Fragment {
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";

    public static final String IMAGE_IS_EDIT = "image_is_edit";

    public static final int MODE_NONE = 0;
    public static final int MODE_STICKERS = 1;// 贴图模式
    public static final int MODE_TEXT = 5;// 文字模式

    public String saveFilePath;// 生成的新图片路径
//    private int imageWidth, imageHeight;// 展示图片控件 宽 高
//    private LoadImageTask mLoadImageTask;

    public int mode = MODE_NONE;// 当前操作模式

    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;

    private EditImageFragment mContext;
    private Bitmap mainBitmap;// 底层显示Bitmap
    public ImageViewTouch mainImage;
//    private View backBtn;

//    public ViewFlipper bannerFlipper;
//    private View applyBtn;// 应用按钮
//    private View saveBtn;// 保存按钮

//    public AddImageGroupView mAddImageGroupView;// 贴图层View
//    public AddTextItemView mAddTextItemView;//文本贴图显示View

    private SaveImageTask mSaveImageTask;

    private RedoUndoController mRedoUndoController;//撤销操作

    /**
     * @param context
     * @param editImagePath
     * @param outputPath
     * @param requestCode
     */
    public static void start(Activity context, final String editImagePath, final String outputPath, final int requestCode) {
        if (TextUtils.isEmpty(editImagePath)) {
            Toast.makeText(context, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(context, EditImageFragment.class);
        it.putExtra(EditImageFragment.FILE_PATH, editImagePath);
        it.putExtra(EditImageFragment.EXTRA_OUTPUT, outputPath);
        context.startActivityForResult(it, requestCode);
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //checkInitImageLoader();
//        setContentView(R.layout.activity_image_edit);
//
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_image_edit, container, false);
    }

    ActivityImageEditBinding mBinding ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding =  DataBindingUtil.bind(view);
        initView();
        getData();
    }

    private void getData() {
        saveFilePath = new File(getContext().getCacheDir().getAbsolutePath(), "tietu" + System.currentTimeMillis() + ".png").getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_editor);
        changeMainBitmap(bitmap, false);
    }

    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        imageWidth = metrics.widthPixels / 2;
//        imageHeight = metrics.heightPixels / 2;

//        bannerFlipper = (ViewFlipper) findViewById(R.id.banner_flipper);
        mBinding.bannerFlipper.setInAnimation(getContext(), R.anim.in_bottom_to_top);
        mBinding.bannerFlipper.setOutAnimation(getContext(), R.anim.out_bottom_to_top);

//        applyBtn = findViewById(R.id.apply);
        mBinding.apply.setOnClickListener(new ApplyBtnClick());
//        saveBtn = findViewById(R.id.save_btn);
        mBinding.saveBtn.setOnClickListener(new SaveBtnClick());

//        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
//        mBinding.backBtn = findViewById(R.id.back_btn);// 退出按钮
        mBinding.backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();

            }
        });

//        mAddImageGroupView = (AddImageGroupView) findViewById(R.id.sticker_panel);
        mainImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.stickerPanel.setRootImageRect(mainImage.getRootImageRect(), mainImage.getWidth(), mainImage.getHeight());
            }
        });


//        mAddTextItemView = (AddTextItemView) findViewById(R.id.text_sticker_panel);

//        Button buttonImage = findViewById(R.id.btn_image);
//        Button buttonText = findViewById(R.id.btn_text);
        mBinding.btnImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mBinding.stickerPanel.setVisibility(View.VISIBLE);
                mBinding.textStickerPanel.setVisibility(View.GONE);
                Bitmap bitmap =  getImageFromAssetsFile("stickers/type1/1.png");
                mBinding.stickerPanel.addBitImage(bitmap);
            }
        });

        mBinding.btnText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.stickerPanel.setVisibility(View.GONE);
                mBinding.textStickerPanel.setVisibility(View.VISIBLE);
//                mAddTextItemView.setText("哈哈哈哈");
                mBinding.textStickerPanel.setText("哈哈哈哈\n嘻嘻嘻嘻嘻嘻\n啦啦啦啦啦啦");
            }
        });

        mainImage.setFlingListener(new ImageViewTouch.OnImageFlingListener() {
            @Override
            public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > 1) {
                    closeInputMethod();
                }
            }
        });

        mRedoUndoController = new RedoUndoController(this, mBinding.redoUodoPanel);
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 关闭输入法
     */
    private void closeInputMethod() {
//        if (mAddTextFragment.isAdded()) {
//            mAddTextFragment.hideInput();
//        }
    }

//    @Override
//    public void onBackPressed() {
//        switch (mode) {
//            case MODE_STICKERS:
////                mStickerFragment.backToMain();
//                return;
//            case MODE_TEXT:
////                mAddTextFragment.backToMain();
//                return;
//        }
//
//        if (canAutoExit()) {
//            onSaveTaskDone();
//        } else {//图片还未被保存    弹出提示框确认
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//            alertDialogBuilder.setMessage(R.string.exit_without_save)
//                    .setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            mContext.finish();
//                        }
//                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
//        }
//    }

    /**
     * 应用按钮点击
     *
     * @author panyi
     */
    private final class ApplyBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (mode) {
                case MODE_STICKERS:
//                    mStickerFragment.applyStickers();// 保存贴图
                    break;
                case MODE_TEXT://文字贴图 图片保存
//                    mAddTextFragment.applyTextImage();
                    break;
                default:
                    break;
            }// end switch
        }
    }// end inner class

    /**
     * 保存按钮 点击退出
     *
     * @author panyi
     */
    private final class SaveBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mOpTimes == 0) {//并未修改图片
                onSaveTaskDone();
            } else {
                doSaveImage();
            }
        }
    }// end inner class

    protected void doSaveImage() {
        if (mOpTimes <= 0)
            return;

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
    }

    /**
     * @param newBit
     * @param needPushUndoStack
     */
    public void changeMainBitmap(Bitmap newBit, boolean needPushUndoStack) {
        if (newBit == null)
            return;

        if (mainBitmap == null || mainBitmap != newBit) {
            if (needPushUndoStack) {
                mRedoUndoController.switchMainBit(mainBitmap, newBit);
                increaseOpTimes();
            }
            mainBitmap = newBit;
            mainImage.setImageBitmap(mainBitmap);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        if (mRedoUndoController != null) {
            mRedoUndoController.onDestroy();
        }
    }


    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    protected void onSaveTaskDone() {
        //tODO:
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(FILE_PATH, filePath);
//        returnIntent.putExtra(EXTRA_OUTPUT, saveFilePath);
//        returnIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);
//
//        FileUtil.ablumUpdate(this, saveFilePath);
//        setResult(RESULT_OK, returnIntent);
//        finish();
    }

    /**
     * 保存图像
     * 完成后退出
     */
    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
//        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath))
                return false;

            return BitmapUtils.saveBitmap(params[0], saveFilePath);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
//            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = EditImageActivity.getLoadingDialog(mContext, R.string.saving_image, false);
//            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
//            dialog.dismiss();

            if (result) {
                resetOpTimes();
                onSaveTaskDone();
            } else {
                Toast.makeText(getContext(), R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        }
    }//end inner class

    public Bitmap getMainBit() {
        return mainBitmap;
    }

}
