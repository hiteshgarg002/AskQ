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
import com.hrrock.askq.models.HomeNewModel;
import com.hrrock.askq.networks.VolleyConnect;
import com.marcoscg.dialogsheet.DialogSheet;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class HomeNewAdapter extends RecyclerView.Adapter<HomeNewAdapter.ViewHolder> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private JsonArrayRequest jsonArrayRequest;
    private SharedPreferences preferences;
    private List<HomeNewModel> list;
    private static final String TOPIC = "topic";
    private static final String USER_PREFERENCES = "userinfo";
    private static final int LAYOUT = R.layout.layout_home_new_item;
    private DialogSheet deleteDialog;

    public HomeNewAdapter(Context ctx, List<HomeNewModel> list) {
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

            statusTag = itemView.findViewById(R.id.statusTagOnHomeNew);
            followStatus = itemView.findViewById(R.id.followStatusOnHomeNew);
            topic = itemView.findViewById(R.id.topicNameOnHomeNew);
            userName = itemView.findViewById(R.id.userNameOnHomeNew);
            question = itemView.findViewById(R.id.questionOnHomeNew);
            voteCount = itemView.findViewById(R.id.voteCountOnHomeNew);
            tagUpvote = itemView.findViewById(R.id.upvoteTagOnHomeNew);
            upvoteCountSeperator = itemView.findViewById(R.id.upvoteCountSeperatorOnHomeNew);
            linearAnswer = itemView.findViewById(R.id.linearAnswerOnHomeNew);
            linearUpvote = itemView.findViewById(R.id.linearVoteOnHomeNew);
            linearVoteCount = itemView.findViewById(R.id.linearVoteCountOnHomeNew);
            relBookmark = itemView.findViewById(R.id.relBookmarkOnHomeNew);
            relFollowStatus = itemView.findViewById(R.id.relFollowStatusOnHomeNew);
            relDelete = itemView.findViewById(R.id.relDeleteOnHomeNew);
            iconUpvote = itemView.findViewById(R.id.upvoteIconOnHomeNew);
            iconBookmark = itemView.findViewById(R.id.bookmarkIconOnHomeNew);
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
        final HomeNewModel newModel = list.get(position);

        if (Objects.equals(newModel.getUsername(), preferences.getString("username", ""))) {
            holder.relFollowStatus.setVisibility(View.GONE);
            holder.relBookmark.setVisibility(View.GONE);
            holder.relDelete.setVisibility(View.VISIBLE);
        } else {
            holder.relDelete.setVisibility(View.GONE);
            holder.relFollowStatus.setVisibility(View.VISIBLE);
            holder.relBookmark.setVisibility(View.VISIBLE);
        }


        if (!Objects.equals(newModel.getFollowStatus(), "null")) {
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

        if (Integer.parseInt(newModel.getVoteCount()) > 0) {
            if (!Objects.equals(newModel.getUpVote(), "null")) {
                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            }
            holder.voteCount.setText(newModel.getVoteCount());
            holder.linearVoteCount.setVisibility(View.VISIBLE);
        }

        if (!Objects.equals(newModel.getUpVote(), "null")) {
            holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
        }

        if (!Objects.equals(newModel.getBookmarkStatus(), "null")) {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
        } else {
            holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
        }

        holder.userName.setText(newModel.getUsername());
        holder.question.setText(newModel.getQuestion());
        holder.topic.setText(newModel.getTopic());

        PushDownAnim.setPushDownAnimTo(holder.linearUpvote)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (!Objects.equals(newModel.getUpVote(), "null")) {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                    } else {
                        holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                        holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    }
                    upVote(holder, newModel);
                });

        PushDownAnim.setPushDownAnimTo(holder.linearAnswer)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {

                });

        PushDownAnim.setPushDownAnimTo(holder.relBookmark)
                .setScale(PushDownAnim.MODE_SCALE, 1.10f)
                .setOnClickListener(view -> {
                    if (!Objects.equals(newModel.getBookmarkStatus(), "null")) {
                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                    } else {
                        holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.black, null)));
                    }

                    bookmark(holder, newModel, view);
                });

        PushDownAnim.setPushDownAnimTo(holder.relFollowStatus)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    if (holder.relFollowStatus.getTag() == "follow") {
                        follow(newModel, holder);
                    } else if (holder.relFollowStatus.getTag() == "following") {
                        unFollow(newModel, holder);
                    }
                });

        PushDownAnim.setPushDownAnimTo(holder.relDelete)
                .setScale(PushDownAnim.MODE_SCALE, 1.05f)
                .setOnClickListener(view -> {
                    deleteDialog(newModel.getAskId(), position, view);
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void upVote(ViewHolder holder, HomeNewModel newModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/upVote?username=" + preferences.getString("username", "") + "&askid=" + newModel.getAskId();

        stringRequest = new StringRequest(url, response -> {
            updateUpVoteCount(holder, newModel);
            //Toast.makeText(ctx, "" + response, Toast.LENGTH_SHORT).show();

            if (Objects.equals(response, "success")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                holder.voteCount.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.upvoteCountSeperator.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(newModel.getUpVote(), "null")) {
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
            if (!Objects.equals(newModel.getUpVote(), "null")) {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
            } else {
                holder.iconUpvote.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                holder.tagUpvote.setTextColor(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }
        });

        requestQueue.add(stringRequest);
    }

    private void updateUpVoteCount(ViewHolder holder, HomeNewModel newModel) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FeedController/getUpVoteCount?askid=" + newModel.getAskId();

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
    private void follow(HomeNewModel newModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/follow?follower=" + preferences.getString("username", "") + "&following=" + newModel.getTopicId() + "&category=" + TOPIC;

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
    private void unFollow(HomeNewModel newModel, ViewHolder holder) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/FollowController/unFollow?follower=" + preferences.getString("username", "") + "&following=" + newModel.getTopicId() + "&category=" + TOPIC;

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

    private void bookmark(ViewHolder holder, HomeNewModel newModel, View view) {
        final String url = "http://" + ctx.getString(R.string.ip) + "/AskQ/index.php/AskController/addBookmark?username=" + preferences.getString("username", "") + "&askid=" + newModel.getAskId();

        stringRequest = new StringRequest(url, response -> {

            if (Objects.equals(response, "success")) {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));

                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_SHORT).show();
            } else if (Objects.equals(response, "failed")) {
                if (!Objects.equals(newModel.getBookmarkStatus(), "null")) {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(android.R.color.holo_blue_dark, null)));
                } else {
                    holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
                }

                Snackbar.make(view, "Error! try again.", Snackbar.LENGTH_SHORT).show();
            } else {
                holder.iconBookmark.setImageTintList(ColorStateList.valueOf(ctx.getResources().getColor(R.color.default_holo_light, null)));
            }
        }, error -> {
            if (!Objects.equals(newModel.getBookmarkStatus(), "null")) {
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

    public void addAsks(List<HomeNewModel> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}
