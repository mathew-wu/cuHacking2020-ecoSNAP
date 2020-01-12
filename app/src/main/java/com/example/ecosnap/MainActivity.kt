package com.example.ecosnap


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ecosnap.utils.ImageClassifier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.another_view.view.*



class MainActivity : AppCompatActivity() {
    private lateinit var classifier: ImageClassifier
    private lateinit var photoImage: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_switch.setOnClickListener {
            startActivity(Intent(this, LeaderBoardActivity::class.java))
        }
        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(i, 123)
        btn_cam.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 123)
        }



        // Set a click listener for button widget
        txt_v.setOnClickListener{
            // Initialize a new layout inflater instance
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.another_view,null)
            val db = FirebaseFirestore.getInstance()
            val itemsCollection = db.collection("Items")
            itemsCollection.orderBy("description", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
                var i = 0
                for (document in result) {
                    i++
                    Log.d("MESSAGE", "${document.id} => ${document.data}")
                    var a = document.data.get("description")

                    view.text_view.text = a.toString()

                }
            }


            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }


            // If API level 23 or higher then execute the code
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.RIGHT
                popupWindow.exitTransition = slideOut

            }

            // Get the widgets reference from custom view
            val tv = view.findViewById<TextView>(R.id.text_view)
            val buttonPopup = view.findViewById<Button>(R.id.button_popup)


            // Set a click listener for popup's button widget
            buttonPopup.setOnClickListener{
                // Dismiss the popup window
                popupWindow.dismiss()
            }

            // Set a dismiss listener for popup window
            popupWindow.setOnDismissListener {
                Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
            }


            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(root_layout)
            popupWindow.showAtLocation(
                root_layout, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123) {
            var bmp = data?.extras?.get("data") as Bitmap
            iv_camera.setImageBitmap(bmp)
            photoImage = bmp
            classifier = ImageClassifier(getAssets())
            classifier.recognizeImage(photoImage).subscribeBy(
                onSuccess = {
                    Log.d("Message", it.toString())
                }
            )
            val txt = "Recyclable content"
            txt_v.setText("${txt}")
        }
    }
}