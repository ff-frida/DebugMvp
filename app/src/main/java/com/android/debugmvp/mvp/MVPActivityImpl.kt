package com.android.debugmvp.mvp

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.android.debugmvp.R
import com.debug.framework.mvp.AbsMVPActivity
import com.debug.framework.mvp.IBasePresenter
import com.hi.dhl.binding.viewbind
import org.greenrobot.eventbus.EventBus
import sj.mblog.LL

/**
 * @author dr
 * @date 2020-05-22
 * @description 如果需要增加公用方法, 可以在这里增加
 */
abstract class MVPActivityImpl<P : IBasePresenter<*>> : AbsMVPActivity<P>() {

    var TAG: String = javaClass.simpleName
    lateinit var mContext: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //延申到刘海
        if (applyEdges() && Build.VERSION.SDK_INT >= 28) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        if (registerEventBus()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        }
        mContext = this
        LL.i(TAG, "onCreate: ->>>")
        initView()
        initData(intent)
    }

    protected abstract fun initView()

    open fun registerEventBus(): Boolean {
        return false
    }

    open fun applyEdges(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        LL.i(TAG, "onDestroy: ->>>")
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun showToast(msg: String) {
        if (TextUtils.isEmpty(msg)) {
            Log.e("->>", Log.getStackTraceString(Throwable()))
            return
        }
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(msg: Int) {
        Toast.makeText(this, "" + getString(msg), Toast.LENGTH_SHORT).show()
    }

    override fun showToastLong(msg: String) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_LONG).show()
    }

    override fun showToastLong(msg: Int) {
        Toast.makeText(this, "" + getString(msg), Toast.LENGTH_LONG).show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.fontScale != 1f) {
            //fontScale不为1，需要强制设置为1
            resources
        }
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        if (resources.configuration.fontScale != 1f) {
            //fontScale不为1，需要强制设置为1
            val newConfig = Configuration()
            newConfig.setToDefaults()
            //设置成默认值，即fontScale为1
            resources.updateConfiguration(newConfig, resources.displayMetrics)
        }
        return resources
    }

    override fun onStop() {
        super.onStop()
        LL.i(TAG, "onStop: ->>>")
    }

    override fun onResume() {
        super.onResume()
        LL.i(TAG, "onResume: ->>>")
    }
    //@Subscribe(threadMode = ThreadMode.MAIN)
    //public void onTokenInvalid(EventTokenInvalid e) {
    //    String str = getTopActivity(this);
    //    if (str != null && str.contains(" .LoginActivity")) {
    //        //当前栈顶为LoginActivity，则不处理。否则跳转到登录页面
    //        LL.e(TAG, "当前页面已经是登录页");
    //    } else {
    //        LL.e(TAG, "当前LoginActiity是否栈顶：false-----：跳转到登录页面");
    //        showToastLong("登录失效，请重新登录");
    //        Intent intent = new Intent(this, LoginActivity.class);
    //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //        this.startActivity(intent);
    //    }
    //}
    /**
     * 获得栈中最顶层的Activity
     *
     * @param context
     * @return
     */
    private fun getTopActivity(context: Context): String? {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        return if (runningTaskInfos != null) {
            runningTaskInfos[0].topActivity.toString()
        } else {
            null
        }
    }

    fun dismissLoading() {
        //WaitDialog.dismiss();
    }

    protected fun getStringRes(res: Int): String {
        return resources.getString(res)
    }

    protected val isActive: Boolean
        get() = lifecycle.currentState != Lifecycle.State.DESTROYED

    protected fun <T : View?> find(viewId: Int): T {
        return findViewById<View>(viewId) as T
    }
}