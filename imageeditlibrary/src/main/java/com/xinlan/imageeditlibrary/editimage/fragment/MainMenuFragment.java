//package com.xinlan.imageeditlibrary.editimage.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.xinlan.imageeditlibrary.R;
//import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
//
//
///**
// * 工具栏主菜单
// *
// * @author panyi
// */
//public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
//    public static final int INDEX = ModuleConfig.INDEX_MAIN;
//
//    public static final String TAG = MainMenuFragment.class.getName();
//    private View mainView;
//
//    private View stickerBtn;// 贴图按钮
//    private View mTextBtn;//文字型贴图添加
//
//    public static MainMenuFragment newInstance() {
//        MainMenuFragment fragment = new MainMenuFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
//                null);
//        return mainView;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        stickerBtn = mainView.findViewById(R.id.btn_stickers);
//        mTextBtn = mainView.findViewById(R.id.btn_text);
//
//        stickerBtn.setOnClickListener(this);
//        mTextBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onShow() {
//        // do nothing
//    }
//
//    @Override
//    public void backToMain() {
//        //do nothing
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == stickerBtn) {
//            onStickClick();
//        }  else if (v == mTextBtn) {
//            onAddTextClick();
//        }
//    }
//
//    /**
//     * 贴图模式
//     *
//     * @author panyi
//     */
//    private void onStickClick() {
//        activity.bottomGallery.setCurrentItem(StickerFragment.INDEX);
//        activity.mStickerFragment.onShow();
//    }
//
//
//
//    /**
//     * 插入文字模式
//     *
//     * @author panyi
//     */
//    private void onAddTextClick() {
//        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
//        activity.mAddTextFragment.onShow();
//    }
//
//}// end class
