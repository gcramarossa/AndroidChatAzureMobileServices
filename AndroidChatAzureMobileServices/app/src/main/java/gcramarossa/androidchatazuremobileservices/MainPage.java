package gcramarossa.androidchatazuremobileservices;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

public class MainPage extends ActionBarActivity {

    private MobileServiceClient _client;
    private ArrayList<String> _messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        try
        {
            _client = new MobileServiceClient("https://provachat.azurewebsites.net", this);
        }
        catch (MalformedURLException ex)
        {

        }

        final Button bottone = (Button) findViewById(R.id.sendMessage);
        final TextView user = (TextView) findViewById(R.id.user);
        final TextView message = (TextView) findViewById(R.id.messageText);
        final ListView messageList = (ListView) findViewById(R.id.messages);
        final TextView messageIdentifier = (TextView) findViewById(R.id.messageIdentifier);

        final MessageItem nuovoMessaggio = new MessageItem();
        //nuovoMessaggio.ID = getID();

        bottone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Premuto", Toast.LENGTH_LONG).show();
                messageIdentifier.setText("");
                final MessageItem nuovoMessaggio = new MessageItem();
                final ArrayAdapter<MessageActivity> items = new ArrayAdapter<MessageActivity>(getApplicationContext(), R.layout.activity_message, R.id.id, createItem())
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {

                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View newItem = inflater.inflate(R.layout.activity_message, null);
                        TextView id = (TextView) newItem.findViewById(R.id.id);
                        TextView username = (TextView) newItem.findViewById(R.id.userText);
                        TextView userMessage = (TextView) newItem.findViewById(R.id.userMessage);
                        String[] params = _messages.get(position).split(" : ");
                        id.setText(params[0]);
                        username.setText(params[1]);
                        userMessage.setText(params[2]);
                        return newItem;

                    }
                };
                nuovoMessaggio.User = user.getText().toString();
                nuovoMessaggio.Text = message.getText().toString();
                _client.getTable(MessageItem.class).insert(nuovoMessaggio, new TableOperationCallback<MessageItem>() {
                    @Override
                    public void onCompleted(final MessageItem messageItem, Exception e1, ServiceFilterResponse serviceFilterResponse) {
                        if (e1 == null) {
                            Toast.makeText(getApplicationContext(), "Inserimento completato", Toast.LENGTH_LONG).show();
                            messageIdentifier.setText(messageItem.ID);
                            _messages.add(messageIdentifier.getText().toString() + " : " + user.getText().toString() + " : " +message.getText().toString());

                        } else {
                            Toast.makeText(getApplicationContext(), "Inserimento non completato" + e1.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //messageList.setAdapter(items);
                messageList.setAdapter(items);
                messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView messageId = (TextView) view.findViewById(R.id.id);
                        TextView username = (TextView) view.findViewById(R.id.userText);
                        TextView userMessage = (TextView) view.findViewById(R.id.userMessage);
                        _client.getTable(MessageItem.class).delete(messageId.getText().toString());
                        Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private MessageActivity[] createItem() {
        MessageActivity[] item = new MessageActivity[_messages.size()];
        for (int i = 0; i < _messages.size(); i++)
        {
            item[i] = new MessageActivity();
        }

        return item;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
