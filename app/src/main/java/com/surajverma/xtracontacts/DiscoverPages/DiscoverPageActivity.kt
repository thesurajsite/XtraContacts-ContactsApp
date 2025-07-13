package com.surajverma.xtracontacts.DiscoverPages

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.surajverma.xtracontacts.R
import com.surajverma.xtracontacts.databinding.ActivityDiscoverPageBinding

class DiscoverPageActivity : AppCompatActivity() {

  private lateinit var binding: ActivityDiscoverPageBinding
  private lateinit var arrDiscoverPageList: ArrayList<DiscoverPageDataClass>
  val viewModel : DiscoverPagesViewModel = DiscoverPagesViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDiscoverPageBinding.inflate(layoutInflater)
    setContentView(binding.root)

    arrDiscoverPageList = ArrayList<DiscoverPageDataClass>()
    val recyclerAdapter = DiscoverPageRecyclerAdapter(this, arrDiscoverPageList, viewModel)
    binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
    binding.recyclerView.adapter = recyclerAdapter

//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))
//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))
//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))
//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))
//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))
//    arrDiscoverPageList.add(DiscoverPageDataClass("Page 1", "fghj", "rtygdvhbj"))

    recyclerAdapter.notifyDataSetChanged()

    binding.Progressbar.visibility=View.VISIBLE
    viewModel.fetchDiscoverPages(this)
    viewModel.contactPages.observe(this) { list->
      arrDiscoverPageList.clear()
      arrDiscoverPageList.addAll(list)
      binding.Progressbar.visibility=View.GONE
      recyclerAdapter.notifyDataSetChanged()
    }

    binding.floatingActionButton.setOnClickListener {
      val dialogView = LayoutInflater.from(this).inflate(R.layout.add_discoverpage_dialog, null)
      val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
      val alertDialog = dialogBuilder.create()
      alertDialog.show()

      val btnAdd = dialogView.findViewById<Button>(R.id.addButton)
      val dialogEditText = dialogView.findViewById<EditText>(R.id.pageIdEditText)

      btnAdd.setOnClickListener {
        val pageId = dialogEditText.text.toString()
        viewModel.AddPage(pageId, this) { onSuccess->
          if(onSuccess){
            alertDialog.dismiss()
            // Fetch Pages to refresh
            viewModel.fetchDiscoverPages(this)
          }
        }
      }
    }



  }
}
