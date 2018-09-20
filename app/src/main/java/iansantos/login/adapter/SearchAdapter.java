package iansantos.login.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import iansantos.login.R;
import iansantos.login.model.StackOverflowQuestion;

public class SearchAdapter extends RecyclerView.Adapter {

    private List<StackOverflowQuestion> questionList;

    public SearchAdapter(List<StackOverflowQuestion> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemList = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.questions_list, viewGroup, false);
        return new QuestionViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        QuestionViewHolder questionViewHolder = (QuestionViewHolder) viewHolder;
        StackOverflowQuestion question = questionList.get(i);
        questionViewHolder.title.setText(question.getTitle());
        questionViewHolder.link.setText(question.getLink());
        questionViewHolder.name.setText(question.getOwner().getName());
        questionViewHolder.tags.setText(TextUtils.join(", ", question.getTags()));
        if (question.isAnswered()) {
            questionViewHolder.isAnswered.setText(R.string.answered);
        } else {
            questionViewHolder.isAnswered.setText(R.string.not_answered);
        }
        Date date = new Date(question.getCreationDate() * 1000L);
        String formattedDate = DateFormat.getDateTimeInstance().format(date);
        questionViewHolder.creationDate.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView link;
        TextView name;
        TextView isAnswered;
        TextView tags;
        TextView creationDate;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_textView);
            link = itemView.findViewById(R.id.link_textView);
            name = itemView.findViewById(R.id.name_textView);
            isAnswered = itemView.findViewById(R.id.is_answered_textView);
            tags = itemView.findViewById(R.id.tags_textView);
            creationDate = itemView.findViewById(R.id.creation_date_textView);
        }
    }

}
