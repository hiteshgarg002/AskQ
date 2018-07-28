package com.hrrock.askq.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.hrrock.askq.activities.ReplyActivity;
import com.hrrock.askq.models.HomeFeedModel;
import com.hrrock.askq.networks.VolleyConnect;
import com.marcoscg.dialogsheet.DialogSheet;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import spencerstudios.com.bungeelib.Bungee;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private JsonArrayRequest jsonArrayRequest;
    private SharedPreferences preferences;
    private List<HomeFeedModel> list;
    private static final String TOPIC = "topic";
    private static final String USER_PREFERENCES = "userinfo";
    private static final int LAYOUT = R.layout.layout_home_feed_item;
    private DialogSheet deleteDialog;

    public HomeFeedAdapter(Context ctx, List<HomeFeedModel> list) {
        this.ctx = ctx;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.requestQueue = VolleyConnect.getInstance().getRequestQueue();
        this.preferences = ctx.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView statusTag, followStatus, topic, userName, question, voteCount, tagUpvote, upvoteCountSeperator;
        private RelativeLayout relBookmark, relFollowStatus, relDelete;
        private LinearLayout linearAnswer, linearUpvote, linearVoteCount;
        private ImageView iconUpvote, iconBookmark;

        ViewHolder(View itemView) {
            super(itemView);

            statusTag = itemView.findViewById(R.id.statusTagOnHomeFeed);
            followStatus = itemView.findViewById(R.id.followStatusOnHomeFeed);
            topic = itemView.findViewById(R.id.topicNameOnHomeFeed);
            userName = itemView.findViewById(R.id.userNameOnHomeFeed);
            question = itemView.findViewById(R.id.questionOnHomeFeed);
            voteCount = itemView.findViewById(R.id.voteCountOnHomeFeed);
            tagUpvote = itemView.findViewById(R.id.upvoteTagOnHomeFeed);
            upvoteCountSeperator = itemView.findViewById(R.id.upvoteCountSeperatorOnHomeFeed);
            linearAnswer = itemView.findViewById(R.id.linearAnswerOnHomeFeed);
            linearUpvote = itemView.findViewById(R.id.linearVoteOnHomeFeed);
            linearVoteCount = itemView.findViewById(R.id.linearVoteCountOnHomeFeed);
            relBookmark = itemView.findViewById(R.id.relBookmarkOnHomeFeed);
            relFollowStatus = itemView.findViewById(R.id.relFollowStatusOnHomeFeed);
            relDelete = itemView.findViewById(R.id.relDeleteOnHomeFeed);
            iconUpvote = itemView.findViewById(R.id.upvoteIconOnHomeFeed);
            iconBookmark = itemView.findViewById(R.id.bookmarkIconOnHomeFeed);
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
        final HomeFeedModel feedModel = list.get(position);

        if (Objects.equals(feedModel.getUsername(), preferences.getString("username", ""))) {
            holder.relFollowStatus.setVisibility(View.GONE);
            holder.relBookmark.setVisibility(View.GONE);
            holder.relDelete.setVisibility(View.VISIBLE);
        } else {
            holder.relDelete.setVisibility(View.GONE);
            holder.relFollowStatus.setVisibility(View.VISIBLE);
            holder.relBookmark.setVisibility(View.VISIBLE);
        }

        if (!Objects.equals(feedModel.getFollowStatus(), "null")) {
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

        if (Integer.parseInt(feedModel.getVoteCount()) > 0) {
            if (!Objects.equals(feedModel.getUpVote(), "null")) {
                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            }
            holder.voteCount.setText(feedModel.getVoteCount());
            holder.linearVoteCount.setVisibility(View.VISIBLE);
        }

        if (!Objects.equals(feedModel.getUpVote(), "null")) {
            holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
        }

        if (!Objects.equals(feedModel.getBookmarkStatus(), "null")) {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
        } else {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
        }

        holder.userName.setText(feedModel.getUsername());
        holder.question.setText(feedModel.getQuestion());
        holder.topic.setText(feedModel.getTopic());

        PushDownAnim.setPushDownAnimTo(holder.linearUpvote)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (!Objects.equals(feedModel.getUpVote(), "null")) {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                    } else {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    }
                    upVote(holder, feedModel);
                });

        PushDownAnim.setPushDownAnimTo(holder.linearAnswer)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> toReply(feedModel.getAskId()));

        PushDownAnim.setPushDownAnimTo(holder.relBookmark)
                .setScale(PushDownAnim.MODE_SCALE, 1.10f)
                .setOnClickListener(view -> {
                    if (!Objects.equals(feedModel.getBookmarkStatus(), "null")) {
                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    } else {
                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                    }

                    bookmark(holder, feedModel, view);
                });

        PushDownAnim.setPushDownAnimTo(holder.relFollowStatus)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (holder.relFollowStatus.getTag() == "follow") {
                        follow(feedModel, holder);
                    } else if (holder.relFollowStatus.getTag() == "following") {
                        unFollow(feedModel, holder);
                    }
                });

        PushDownAnim.setPushDownAnimTo(holder.relDelete)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    deleteDialog(feedModel.getAskId(), position, view);
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void upVote(ViewHolder holder, HomeFeedModel feedModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/upVote?username=" + preferences.getString("username", "") + "&askid=" + feedModel.getAskId();

        stringRequest = new StringRequest(url, response -> {
            updateUpVoteCount(holder, feedModel);
            //Toast.makeText(ctx, "" + response, Toast.LENGTH_SHORT).show();

            if (Objects.equals(response, "success")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(feedModel.getUpVote(), "null")) {
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
            if (!Objects.equals(feedModel.getUpVote(), "null")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }
        });

        requestQueue.add(stringRequest);
    }

    private void updateUpVoteCount(ViewHolder holder, HomeFeedModel feedModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FeedController/getUpVoteCount?askid=" + feedModel.getAskId();

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
    private void follow(HomeFeedModel feedModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/follow?follower=" + preferences.getString("username", "") + "&following=" + feedModel.getTopicId() + "&category=" + TOPIC;

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
    private void unFollow(HomeFeedModel feedModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/unFollow?follower=" + preferences.getString("username", "") + "&following=" + feedModel.getTopicId() + "&category=" + TOPIC;

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

    private void bookmark(ViewHolder holder, HomeFeedModel feedModel, View view) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/addBookmark?username=" + preferences.getString("username", "") + "&askid=" + feedModel.getAskId();

        stringRequest = new StringRequest(url, response -> {

            if (Objects.equals(response, "success")) {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_SHORT).show();
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(feedModel.getBookmarkStatus(), "null")) {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                } else {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                }

                Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_SHORT).show();
            } else {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }

        }, error -> {
            if (!Objects.equals(feedModel.getBookmarkStatus(), "null")) {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }

            Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_SHORT).show();
        });

        requestQueue.add(stringRequest);
    }

    private void delete(String askId, int position, View view) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/deleteAsk?askid=" + askId;

        stringRequest = new StringRequest(url, response -> {
            if (Objects.equals(response, "success")) {
                list.remove(position);
                notifyItemRemoved(position);
            } else {
                Snackbar.make(view, "Couldn't delete! try again.", Snackbar.LENGTH_LONG).show();
            }
        }, error -> {
            Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_LONG).show();
        });

        requestQueue.add(stringRequest);
    }

    private void deleteDialog(String askId, int position, View view) {
        deleteDialog = new DialogSheet(ctx)
                .setTitle(R.string.app_name)
                .setBackgroundColor(ctx.getResources().getColor(R.color.black, null))
                .setMessage("Delete ask?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, v -> {
                    delete(askId, position, view);
                    deleteDialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, viewB -> deleteDialog.dismiss())
                .setButtonsColorRes(R.color.white);

        deleteDialog.show();
    }

    public void addAsks(List<HomeFeedModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    private void toReply(String askId) {
        Intent reply = new Intent(ctx, ReplyActivity.class);
        reply.putExtra("askid", askId);
        ctx.startActivity(reply);
        Bungee.slideLeft(ctx);
    }
}
