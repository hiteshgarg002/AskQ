package com.hrrock.askq.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.baoyz.widget.PullRefreshLayout
import com.hrrock.askq.R
import com.hrrock.askq.activities.AskActivity
import com.hrrock.askq.adapters.HomeBookmarkAdapter
import com.hrrock.askq.models.HomeBookmarkModel
import com.hrrock.askq.networks.VolleyConnect
import com.thekhaeng.pushdownanim.PushDownAnim
import com.wang.avi.AVLoadingIndicatorView
import org.json.JSONArray
import org.json.JSONException
import spencerstudios.com.bungeelib.Bungee
import java.io.UnsupportedEncodingException

class HomeBookmarkFragment : Fragment() {
    private var ctx: Context? = null
    private var adapter: HomeBookmarkAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var preferences: SharedPreferences? = null
    private var requestQueue: RequestQueue? = null
    private var jsonArrayRequest: JsonArrayRequest? = null
    private var list: MutableList<HomeBookmarkModel>? = null
    private var rel: RelativeLayout? = null
    private var relLoading: RelativeLayout?=null
    private var floatingAsk: FloatingActionButton?=null

    private var nestedScrollView: NestedScrollView?=null
    private var layoutManager: LinearLayoutManager? = null
    private var paginationLoading: AVLoadingIndicatorView? = null
    private var isLoading = true
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var previousTotal = 0
    private var viewThreshold = 4
    private var pageNumber = 1
    // private var itemCount = 10

    private companion object {
        private const val USER_PREFERENCE = "userinfo"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home_bookmark, container, false)
        ctx = activity

//        requestQueue = VolleyConnect.getInstance().requestQueue
//        preferences = ctx!!.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE)
//
//        list = ArrayList()
//
//        rel = v.findViewById(R.id.relHomeBookmark)
//        recyclerView = v.findViewById(R.id.homeBookmarkRecyclerView)
//        relLoading=v.findViewById(R.id.relLoadingOnHomeBookmark)
//
//        recyclerView!!.layoutManager = LinearLayoutManager(ctx)
//        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
//        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.recyclerview_divider)!!)
//
//        recyclerView!!.addItemDecoration(itemDecorator)
//        adapter = HomeBookmarkAdapter(ctx!!, list!!)
//
//        getAsks()
//
//        recyclerView!!.adapter = adapter

        requestQueue = VolleyConnect.getInstance().requestQueue
        preferences = ctx!!.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE)

        list = ArrayList()
        rel = v.findViewById(R.id.relHomeBookmark)
        recyclerView = v.findViewById(R.id.homeBookmarkRecyclerView)
        relLoading = v.findViewById(R.id.relLoadingOnHomeBookmark)
        paginationLoading = v.findViewById(R.id.paginationLoadingOHomeBookmark)
        nestedScrollView=v.findViewById(R.id.nestedSVonHomeBookmark)
        floatingAsk=v.findViewById(R.id.floatingAskOnHomeBookmark)

        layoutManager = LinearLayoutManager(ctx)
        recyclerView!!.layoutManager = layoutManager
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.recyclerview_divider)!!)

        recyclerView!!.addItemDecoration(itemDecorator)
        adapter = HomeBookmarkAdapter(ctx!!, list!!)

        getAsks()
        recyclerView!!.adapter = adapter

        onScroll()
        toAsk()

        return v
    }

    private fun getAsks() {
        val postsURL = "http://" + getString(R.string.ip) + "/AskQ/index.php/BookmarkController/getAsks?username=${preferences!!.getString("username", "")}&page_number=$pageNumber"

//        val cache = VolleyConnect.getInstance().requestQueue.cache
//        val entry = cache.get(postsURL)
//
//        if (entry != null) {
//            // fetch the data from cache
//            try {
//                val data = String(entry.data)
//                list!!.clear()
//                cache.clear()
//                val jsonArray = JSONArray(data)
//                for (i in 0 until jsonArray.length()) {
//                    val jsonObject = jsonArray.optJSONObject(i)
//                    val homeBookmarkModel = HomeBookmarkModel(jsonObject.optString("askid"),
//                            jsonObject.optString("topicid"),
//                            jsonObject.optString("username"),
//                            jsonObject.optString("question"),
//                            jsonObject.optString("topicname"),
//                            jsonObject.optString("followstatus"),
//                            jsonObject.optString("votecount"),
//                            jsonObject.optString("bookmarkstatus"),
//                            jsonObject.optString("upvote"))
//
//                    list!!.add(homeBookmarkModel)
//                }
//                adapter!!.notifyDataSetChanged()
//
//                relLoading!!.visibility=View.GONE
//                rel!!.visibility=View.VISIBLE
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        } else {
            jsonArrayRequest = JsonArrayRequest(postsURL, Response.Listener<JSONArray> { response ->
                list!!.clear()
                for (i in 0 until response.length()) {
                    val jsonObject = response.optJSONObject(i)
                    val homeBookmarkModel = HomeBookmarkModel(jsonObject.optString("askid"),
                            jsonObject.optString("topicid"),
                            jsonObject.optString("username"),
                            jsonObject.optString("question"),
                            jsonObject.optString("topicname"),
                            jsonObject.optString("followstatus"),
                            jsonObject.optString("votecount"),
                            jsonObject.optString("bookmarkstatus"),
                            jsonObject.optString("upvote"))

                    list!!.add(homeBookmarkModel)
                }
                adapter!!.notifyDataSetChanged()

                relLoading!!.visibility=View.GONE
                rel!!.visibility=View.VISIBLE
            }, Response.ErrorListener { error ->
            })

            requestQueue!!.add(jsonArrayRequest)
        //}
    }

    private fun onScroll() {
        nestedScrollView!!.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            // Toast.makeText(ctx,"onScroll",Toast.LENGTH_SHORT).show()
            visibleItemCount = layoutManager!!.childCount
            totalItemCount = layoutManager!!.itemCount
            pastVisibleItems = layoutManager!!.findFirstVisibleItemPosition()

            if(v!!.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY) {
                    pageNumber++
                    pagination(pageNumber)
                }
            }

            // detecting whether scrolling up or down for floatingActionButton
            if(scrollY>oldScrollY){
                floatingAsk!!.hide()
            }

            if(scrollY<oldScrollY){
                floatingAsk!!.show()
            }
        }
    }

    private fun pagination(pageNumber:Int) {
        paginationLoading!!.visibility=View.VISIBLE
        val postsURL = "http://" + getString(R.string.ip) + "/AskQ/index.php/BookmarkController/getAsks?username=${preferences!!.getString("username", "")}&page_number=$pageNumber"

        jsonArrayRequest = JsonArrayRequest(postsURL, Response.Listener<JSONArray> { response ->
            if (response.length() > 0) {
                val list = ArrayList<HomeBookmarkModel>()
                for (i in 0 until response.length()) {
                    val jsonObject = response.optJSONObject(i)
                    val homeBookmarkModel = HomeBookmarkModel(jsonObject.optString("askid"),
                            jsonObject.optString("topicid"),
                            jsonObject.optString("username"),
                            jsonObject.optString("question"),
                            jsonObject.optString("topicname"),
                            jsonObject.optString("followstatus"),
                            jsonObject.optString("votecount"),
                            jsonObject.optString("bookmarkstatus"),
                            jsonObject.optString("upvote"))

                    list.add(homeBookmarkModel)
                }
                adapter!!.addAsks(list)
            }
            paginationLoading!!.visibility=View.GONE
        }, Response.ErrorListener { error ->
        })

        requestQueue!!.add(jsonArrayRequest)
    }

    private fun toAsk() {
        PushDownAnim.setPushDownAnimTo(floatingAsk)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    startActivity(Intent(ctx, AskActivity::class.java))
                    Bungee.slideLeft(ctx)
                }
    }
}