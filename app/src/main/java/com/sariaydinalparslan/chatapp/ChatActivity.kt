package com.sariaydinalparslan.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sariaydinalparslan.chatapp.adapter.ChatAdapter
import com.sariaydinalparslan.chatapp.adapter.UserAdapter
import com.sariaydinalparslan.chatapp.model.Chat
import com.sariaydinalparslan.chatapp.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imgBack
import kotlinx.android.synthetic.main.activity_chat.imgProfile
import kotlinx.android.synthetic.main.activity_users.*

class ChatActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    var reference : DatabaseReference ?=null
    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)


        var intent =getIntent()
        var userId = intent.getStringExtra("userId")


        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                tvUserName.text = user!!.userName
                if (user.profileImage == ""){
                    //imgProfile.setImageResource(R.drawable.jolteon)
                }else{
                    Glide.with(this@ChatActivity).load(user.profileImage).into(imgProfile)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        imgBack.setOnClickListener {
            onBackPressed()
        }
        btnSendMessage.setOnClickListener {
            var message : String = etMessage.text.toString()
            if (message.isEmpty()){
                Toast.makeText(applicationContext, "message empty", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            }else{
                sendMessage(firebaseUser!!.uid,userId,message)
                etMessage.setText("")
            }
        }
        readMessage(firebaseUser!!.uid,userId)
    }
    private  fun sendMessage(senderId:String,receiverId :String,message : String){
        var reference :DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        reference!!.child("Chat").push().setValue(hashMap)
        Toast.makeText(applicationContext, "Message Send", Toast.LENGTH_SHORT).show()
    }
    fun readMessage(senderId: String,receiverId: String){
        val databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot : DataSnapshot in snapshot.children){
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)){
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatActivity,chatList)
                chatRecyclerView.adapter =chatAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }
}