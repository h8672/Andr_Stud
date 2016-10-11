package fi.jamk.shoppinglist;

import android.app.LauncherActivity;
import android.app.ListActivity;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xml.sax.Attributes;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        String[] str = {"Hello", "by", "me"};
        String str = "hello";
    }
}
