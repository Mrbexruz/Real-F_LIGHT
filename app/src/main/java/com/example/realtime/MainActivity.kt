package com.example.realtime

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.realtime.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseDatabase= FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("message")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                binding.tvInfo.text = value.toString()
                if (value.toString()== "1"){
                    toggleFlashlight(false)
                }else
                    if (value.toString() == "0"){
                        toggleFlashlight(true)
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "ERROR", Toast.LENGTH_SHORT).show()
            }
        })

        binding.edtInfo.addTextChangedListener {
            reference.setValue(it.toString())
        }

    }

    private fun toggleFlashlight(isFlashOn: Boolean) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                hasFlash
            } ?: throw IllegalStateException("Chiroq mavjud emas!")

            if (isFlashOn) {
                cameraManager.setTorchMode(cameraId, false)
                Toast.makeText(this, "Chiroq o'chirildi", Toast.LENGTH_SHORT).show()
            } else {
                cameraManager.setTorchMode(cameraId, true)
                Toast.makeText(this, "Chiroq yoqildi", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Chiroqni boshqarishda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

}