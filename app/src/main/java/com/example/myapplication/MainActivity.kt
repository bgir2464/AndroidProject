package com.example.myapplication
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.myapplication.core.TAG
class MainActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectivityLiveData: ConnectivityLiveData


    companion object{
        var isON: Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        connectivityManager = getSystemService(android.net.ConnectivityManager::class.java)
        connectivityLiveData = ConnectivityLiveData(connectivityManager)

        connectivityLiveData.observe(this, {
            Log.d(TAG, "connectivityLiveData $it")
            isON=it
            Log.d(TAG, "ison "+isON.toString())
            val tw=findViewById<TextView>(R.id.mode)
           if(it==false){
               if(tw!=null) {

                   tw.text ="OFFLINE"
                   ValueAnimator.ofFloat(0f, 360f).apply {
                       duration = 5000
                       start()
                       addUpdateListener {
                           tw.rotationY = it.animatedValue as Float
                       }
                   }
                   tw.text = "OFFLINE"
               }

           }
            else{

               if(tw!=null)
               {tw.text="ONLINE"
                   ValueAnimator.ofFloat(0f, 360f).apply {
                       duration = 5000
                       start()
                       addUpdateListener {
                           tw.rotationY = -1*it.animatedValue as Float
                       }
                   }
//                   tw.apply {
//                       alpha = 1f
//                       visibility = View.VISIBLE
//                       animate()
//                           .alpha(0f)
//                           .setDuration(5000)
//                           .setListener(null)
//                   }
               }
           }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}