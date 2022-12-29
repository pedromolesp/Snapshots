package com.example.snapshots

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.snapshots.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddFragment : Fragment() {
    private val RC_GALLERY = 18
    private val PATH_SNAPSHOTS = "snapshots"

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var mStorageReferece: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference
    private var mPhotoSelectUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAddBinding.inflate(
            inflater, container, false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.btnPost.setOnClickListener {
            postSnapshot()
        }
        mBinding.btnSelect.setOnClickListener {
            openGallery()
        }
        mStorageReferece = FirebaseStorage.getInstance()
            .reference
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_GALLERY)
    }

    private fun postSnapshot() {
        mBinding.progressBar.visibility = View.VISIBLE
//        mStorageReferece.child(PATH_SNAPSHOTS).child("my_photo")
        val storage = mStorageReferece.child(PATH_SNAPSHOTS).child("my_photo")
        if (mPhotoSelectUri != null)
            storage.putFile(mPhotoSelectUri!!).addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                mBinding.progressBar.progress = progress.toInt()
                mBinding.tvMessage.text = "$progress"
            }
                .addOnCompleteListener {
                    mBinding.progressBar.visibility = View.INVISIBLE
                }.addOnSuccessListener {
                    Snackbar.make(
                        mBinding.root, "Instantánea publicada",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    Snackbar.make(
                        mBinding.root, "Instantánea publicada",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
    }

    private fun saveSnapshot() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GALLERY) {
                mPhotoSelectUri = data?.data
                mBinding.imgPhoto.setImageURI(mPhotoSelectUri)
                mBinding.tilTitle.visibility = View.VISIBLE
                mBinding.btnSelect.visibility = View.INVISIBLE
                mBinding.tvMessage.text = getString(R.string.post_message_valid_title)
            }
        }
    }

}