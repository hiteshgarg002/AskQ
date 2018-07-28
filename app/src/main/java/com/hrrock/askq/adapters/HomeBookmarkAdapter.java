package com.hrrock.askq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.hrrock.askq.R;
import com.hrrock.askq.models.HomeBookmarkModel;
import com.hrrock.askq.networks.VolleyConnect;
import com.marcoscg.dialogsheet.DialogSheet;
import com.skydoves.powermenu.PowerMenu;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class HomeBookmarkAdapter extends RecyclerView.Adapter<HomeBookmarkAdapter.ViewHolder> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private JsonArrayRequest jsonArrayRequest;
    private SharedPreferences preferences;
    private List<HomeBookmarkModel> list;
    private static final String TOPIC = "topic";
    private static final String USER_PREFERENCES = "userinfo";
    private static final int LAYOUT = R.layout.layout_home_bookmark_item;
    private DialogSheet removeDialog;

    public HomeBookmarkAdapter(Context ctx, List<HomeBookmarkModel> list) {
        this.ctx = ctx;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.requestQueue = VolleyConnect.getInstance().getRequestQueue();
        this.preferences = ctx.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView statusTag, followStatus, topic, userName, question, voteCount, tagUpvote, upvoteCountSeperator;
        private RelativeLayout relBookmark, relFollowStatus,relDelete;
        private LinearLayout linearAnswer, linearUpvote, linearVoteCount;
        private ImageView iconUpvote, iconBookmark;

        ViewHolder(View itemView) {
            super(itemView);

            statusTag = itemView.findViewById(R.id.statusTagOnHomeBookmark);
            followStatus = itemView.findViewById(R.id.followStatusOnHomeBookmark);
            topic = itemView.findViewById(R.id.topicNameOnHomeBookmark);
            userName = itemView.findViewById(R.id.userNameOnHomeBookmark);
            question = itemView.findViewById(R.id.questionOnHomeBookmark);
            voteCount = itemView.findViewById(R.id.voteCountOnHomeBookmark);
            tagUpvote = itemView.findViewById(R.id.upvoteTagOnHomeBookmark);
            upvoteCountSeperator = itemView.findViewById(R.id.upvoteCountSeperatorOnHomeBookmark);
            linearAnswer = itemView.findViewById(R.id.linearAnswerOnHomeBookmark);
            linearUpvote = itemView.findViewById(R.id.linearVoteOnHomeBookmark);
            linearVoteCount = itemView.findViewById(R.id.linearVoteCountOnHomeBookmark);
            relBookmark = itemView.findViewById(R.id.relBookmarkOnHomeBookmark);
            relFollowStatus = itemView.findViewById(R.id.relFollowStatusOnHomeBookmark);
           // relDelete=itemView.findViewById(R.id.relDeleteOnHomeBookmark);
            iconUpvote = itemView.findViewById(R.id.upvoteIconOnHomeBookmark);
            iconBookmark = itemView.findViewById(R.id.bookmarkIconOnHomeBookmark);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewHolder holder;
        final View view = layoutInflater.inflate(LAYOUT, parent, false);
        holder = new ViewHolder(view);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HomeBookmarkModel bookmarkModel = list.get(position);

//        if (Objects.equals(bookmarkModel.getUsername(), preferences.getString("username", ""))) {
//            holder.relFollowStatus.setVisibility(View.GONE);
//            holder.relDelete.setVisibility(View.VISIBLE);
//        } else {
//            holder.relDelete.setVisibility(View.GONE);
//            holder.relFollowStatus.setVisibility(View.VISIBLE);
//        }


        if (!Objects.equals(bookmarkModel.getFollowStatus(), "null")) {
            holder.statusTag.setText("Following");
            holder.followStatus.setText("Following");
            holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.white, null));
            holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_black_bg_corner);
            holder.relFollowStatus.setTag("following");
        } else {
            holder.statusTag.setText("Suggestion");
            holder.followStatus.setText("Follow");
            holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.black, null));
            holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_white_bg_black_corner);
            holder.relFollowStatus.setTag("follow");
        }

        if (Integer.parseInt(bookmarkModel.getVoteCount()) > 0) {
            if (!Objects.equals(bookmarkModel.getUpVote(), "null")) {
                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
            }
            holder.voteCount.setText(bookmarkModel.getVoteCount());
            holder.linearVoteCount.setVisibility(View.VISIBLE);
        }

        if (!Objects.equals(bookmarkModel.getUpVote(), "null")) {
            holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
        }

        if (!Objects.equals(bookmarkModel.getBookmarkStatus(), "null")) {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
        } else {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
        }

        holder.userName.setText(bookmarkModel.getUsername());
        holder.question.setText(bookmarkModel.getQuestion());
        holder.topic.setText(bookmarkModel.getTopic());

        PushDownAnim.setPushDownAnimTo(holder.linearUpvote)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (!Objects.equals(bookmarkModel.getUpVote(), "null")) {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                    } else {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    }
                    upVote(holder, bookmarkModel);
                });

        PushDownAnim.setPushDownAnimTo(holder.linearAnswer)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {

                });

        PushDownAnim.setPushDownAnimTo(holder.relBookmark)
                .setScale(PushDownAnim.MODE_SCALE, 1.10f)
                .setOnClickListener(view -> {
//                    if (!Objects.equals(bookmarkModel.getBookmarkStatus(), "null")) {
//                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
//                    } else {
//                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
//                    }

                    removeBookmarkDialog(holder, bookmarkModel, view, position);
                });

        PushDownAnim.setPushDownAnimTo(holder.relFollowStatus)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (holder.relFollowStatus.getTag() == "follow") {
                        follow(bookmarkModel, holder);
                    } else if (holder.relFollowStatus.getTag() == "following") {
                        unFollow(bookmarkModel, holder);
                    }
                });

//        PushDownAnim.setPushDownAnimTo(holder.relDelete)
//                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
//                .setOnClickListener(view -> {
//
//                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void upVote(ViewHolder holder, HomeBookmarkModel bookmarkModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/upVote?username=" + preferences.getString("username", "") + "&askid=" + bookmarkModel.getAskId();

        stringRequest = new StringRequest(url, response -> {
            updateUpVoteCount(holder, bookmarkModel);
            //Toast.makeText(ctx, "" + response, Toast.LENGTH_SHORT).show();

            if (Objects.equals(response, "success")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(bookmarkModel.getUpVote(), "null")) {
                    holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                    holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                } else {
                    holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                }
            } else {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));

                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            }
        }, error -> {
            if (!Objects.equals(bookmarkModel.getUpVote(), "null")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }
        });

        requestQueue.add(stringRequest);
    }

    private void updateUpVoteCount(ViewHolder holder, HomeBookmarkModel bookmarkModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FeedController/getUpVoteCount?askid=" + bookmarkModel.getAskId();

        jsonArrayRequest = new JsonArrayRequest(url, response -> {
            JSONObject jsonObject = response.optJSONObject(0);

            if (Integer.parseInt(jsonObject.optString("votecount")) > 0) {
                holder.voteCount.setText(jsonObject.optString("votecount"));
                holder.linearVoteCount.setVisibility(View.VISIBLE);
            } else {
                holder.linearVoteCount.setVisibility(View.GONE);
            }
        }, error -> {
        });

        requestQueue.add(jsonArrayRequest);
    }

    @SuppressLint("SetTextI18n")
    private void follow(HomeBookmarkModel bookmarkModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/follow?follower=" + preferences.getString("username", "") + "&following=" + bookmarkModel.getTopicId() + "&category=" + TOPIC;

        stringRequest = new StringRequest(url, response -> {
            if (Objects.equals(response, "success")) {
                holder.statusTag.setText("Following");
                holder.followStatus.setText("Following");
                holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.white, null));
                holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_black_bg_corner);
                holder.relFollowStatus.setTag("following");
            } else {
                holder.statusTag.setText("Suggestion");
                holder.followStatus.setText("Follow");
                holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.black, null));
                holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_white_bg_black_corner);
                holder.relFollowStatus.setTag("follow");
            }
        }, error -> {
            holder.statusTag.setText("Suggestion");
            holder.followStatus.setText("Follow");
            holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.black, null));
            holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_white_bg_black_corner);
            holder.relFollowStatus.setTag("follow");
        });

        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void unFollow(HomeBookmarkModel bookmarkModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/unFollow?follower=" + preferences.getString("username", "") + "&following=" + bookmarkModel.getTopicId() + "&category=" + TOPIC;

        stringRequest = new StringRequest(url, response -> {
            if (Objects.equals(response, "success")) {
                holder.statusTag.setText("Suggestion");
                holder.followStatus.setText("Follow");
                holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.black, null));
                holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_white_bg_black_corner);
                holder.relFollowStatus.setTag("follow");
            } else {
                holder.statusTag.setText("Following");
                holder.followStatus.setText("Following");
                holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.white, null));
                holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_black_bg_corner);
                holder.relFollowStatus.setTag("following");
            }
        }, error -> {
            holder.statusTag.setText("Following");
            holder.followStatus.setText("Following");
            holder.followStatus.setTextColor(ctx.getResources().getColor(R.color.white, null));
            holder.relFollowStatus.setBackgroundResource(R.drawable.rounded_black_bg_corner);
            holder.relFollowStatus.setTag("following");
        });

        requestQueue.add(stringRequest);
    }

    private void bookmark(ViewHolder holder, HomeBookmarkModel bookmarkModel, View view, int position) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/addBookmark?username=" + preferences.getString("username", "") + "&askid=" + bookmarkModel.getAskId();

        stringRequest = new StringRequest(url, response -> {

            if (Objects.equals(response, "success")) {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_SHORT).show();
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(bookmarkModel.getBookmarkStatus(), "null")) {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                } else {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                }

                Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_SHORT).show();
            } else {
                // holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                list.remove(position);
                notifyItemRemoved(position);
            }
        }, error -> {
            if (!Objects.equals(bookmarkModel.getBookmarkStatus(), "null")) {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }

            Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_SHORT).show();
        });

        requestQueue.add(stringRequest);
    }

    private void removeBookmarkDialog(ViewHolder holder, HomeBookmarkModel bookmarkModel, View view, int position) {
        removeDialog = new DialogSheet(ctx)
                .setTitle(R.string.app_name)
                .setBackgroundColor(ctx.getResources().getColor(R.color.black, null))
                .setMessage("Remove ask?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, v -> {
                    bookmark(holder, bookmarkModel, view, position);
                    removeDialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, viewB -> removeDialog.dismiss())
                .setButtonsColorRes(R.color.white);

        removeDialog.show();
    }

    public void addAsks(List<HomeBookmarkModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}
