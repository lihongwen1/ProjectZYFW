package com.bigcreate.zyfw.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.bigcreate.library.startActivity
import com.bigcreate.zyfw.R
import com.bigcreate.zyfw.activities.ProjectDetailsActivity
import com.bigcreate.zyfw.adapter.FavoriteListAdapter
import com.bigcreate.zyfw.adapter.ProjectListAdapter
import com.bigcreate.zyfw.base.Attributes
import com.bigcreate.zyfw.datasource.ReleasedListDataSource
import com.bigcreate.zyfw.models.Project
import com.bigcreate.zyfw.models.SearchModel
import com.bigcreate.zyfw.viewmodel.NetworkStateViewModel
import kotlinx.android.synthetic.main.fragment_my_favorite.*

class MyReleasedFragment : Fragment() {

    private lateinit var networkStateViewModel: NetworkStateViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.putString("title", "加入的项目")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        networkStateViewModel = ViewModelProvider(this)[NetworkStateViewModel::class.java]
        refreshList()
    }
    private fun refreshList() {

        val adapter = ProjectListAdapter() { position,item ->
            startActivity<ProjectDetailsActivity> {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(Uri.parse(String.format(Attributes.authorityProject, item.projectId)), "prpject/${item.projectTopic}")
                putExtra("projectId",item.projectId)
            }
        }
        listMyFavorite.adapter = adapter
        adapter.submitList(PagedList.Builder<Int, SearchModel>(ReleasedListDataSource(networkStateViewModel.state), PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(20)
                .build())
                .setNotifyExecutor {
                    Handler(Looper.getMainLooper()).post(it)
                }.setFetchExecutor {
                    Attributes.backgroundExecutors.execute(it)
                }.build())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MyJoinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyReleasedFragment().apply {
                    arguments = Bundle().apply {
                        putString("title", "发布的项目")
                    }
                }
    }
}