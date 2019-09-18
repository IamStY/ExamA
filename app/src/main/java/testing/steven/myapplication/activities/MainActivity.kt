package testing.steven.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import testing.steven.myapplication.R
import testing.steven.myapplication.adapters.OpenDataRecyclerAdapter
import testing.steven.myapplication.api.ApiRequestManager
import testing.steven.myapplication.api.ICallback_Notify
import testing.steven.myapplication.datamodels.OpenDataModel
import testing.steven.myapplication.utils.StevensSimpleRecyclerPaging
import testing.steven.myapplication.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), StevensSimpleRecyclerPaging.ICallbackLoadMoreData {
    override fun loadmoreData() {
        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        model.fetchData(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()

    }

    private fun initView() {

        var openDataRecyclerAdapter = OpenDataRecyclerAdapter(ArrayList<OpenDataModel>())
        prv_open_list.adapter = openDataRecyclerAdapter
        prv_open_list.layoutManager = LinearLayoutManager(this)
        prv_open_list.init(this)
        prv_open_list.setHasFixedSize(true)


    }

    private fun loadingIndicator(loading: Boolean?) {

        if (loading == true) {
            av_loading.visibility = View.VISIBLE
        } else {
            av_loading.visibility = View.GONE
        }
    }

    private fun assignAdapterData(data: ArrayList<OpenDataModel>) {
        var adapter = prv_open_list.adapter as OpenDataRecyclerAdapter
        adapter.setDataArrayList(data)
    }

    private fun initData() {

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        model.bindRequestIndicator().observe(this, Observer { loading ->
            loadingIndicator(loading)

        })
        model.bindPagingEnds().observe(this, Observer { pagingEnds ->
            recyclerPagingState(pagingEnds)

        })
        model.fetchData(this).observe(this, Observer<ArrayList<OpenDataModel>> { openDataModels ->
            assignAdapterData(openDataModels)

        })

    }

    private fun recyclerPagingState(pagingEnds: Boolean) {
        if (pagingEnds) {
            prv_open_list.disableHookLoadMore()
        } else {
            prv_open_list.hookLoadMoreAvailable()
        }

    }

}
