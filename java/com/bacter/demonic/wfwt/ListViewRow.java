
package com.bacter.demonic.wfwt;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bacter.demonic.R;

class ListViewRow extends LinearLayout
{
    private static final String LOG_TAG = ListViewRow.class.getSimpleName();

    private MainActivity m_activity;
    private TextView m_textViewStationName;
    private TextView m_textViewAddrAndPing;
    private StateView m_stateView;
    private int m_position;

    public ListViewRow(Context context)
    {
        super(context);
    }

    public ListViewRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListViewRow(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setPressed( boolean pressed )
    {
        super.setPressed( pressed );
        Log.d( LOG_TAG, "setPressed: " + pressed );
        m_activity.onListViewItemPressed( m_position, pressed );
    }

    public void init( MainActivity activity )
    {
        m_activity = activity;
        m_textViewStationName = (TextView) findViewById( R.id.textViewStationName );
        m_textViewAddrAndPing = (TextView) findViewById( R.id.textViewAddrAndPing );
        m_stateView = (StateView) findViewById( R.id.stateView );
    }

    public void setData( int position, String stationName, String addAndPing, int indicatorState )
    {
        m_position = position;
        m_textViewStationName.setText( stationName );
        m_textViewAddrAndPing.setText( addAndPing );
        m_stateView.setIndicatorState( indicatorState );
    }
}
