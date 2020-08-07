package com.bacter.demonic.wfwt;

import org.jsl.collider.RetainableByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

class SessionManager
{
    private static final class Node
    {
        volatile Node prev;
        volatile Node next;
        final ChannelSession session;

        Node(ChannelSession session)
        {
            this.session = session;
        }
    }

    // We do not expect too many channels, let's use just a simple list
    // which will be modified serially under lock,
    // but can be iterated by the audio recorder without lock.

    private final ReentrantLock m_lock;
    private volatile Node m_head;
    private volatile Node m_tail;

    SessionManager()
    {
        m_lock = new ReentrantLock();
    }

    void addSession( ChannelSession channelSession )
    {
        m_lock.lock();
        try
        {
            if (BuildConfig.DEBUG)
            {
                Node node = m_head;
                while (node != null)
                {
                    if (node.session == channelSession)
                        throw new AssertionError();
                    node = node.next;
                }
            }

            final Node node = new Node( channelSession );
            if (m_head == null)
                m_head = node;
            else
            {
                node.prev = m_tail;
                m_tail.next = node;
            }
            m_tail = node;
        }
        finally
        {
            m_lock.unlock();
        }
    }

    void removeSession(ChannelSession channelSession)
    {
        m_lock.lock();
        try
        {
            Node node = m_head;
            while (node != null)
            {
                if (node.session == channelSession)
                {
                    if ((node.next == null) && (node.prev == null))
                    {
                        // only one node in the list
                        if (BuildConfig.DEBUG && ((node != m_head) || (node != m_tail)))
                            throw new AssertionError();
                        m_head = null;
                        m_tail = null;
                    }
                    else if (node.next == null)
                    {
                        // tail node
                        if (BuildConfig.DEBUG && (node != m_tail))
                            throw new AssertionError();
                        node.prev.next = null;
                        m_tail = node.prev;
                    }
                    else if (node.prev == null)
                    {
                        // head node
                        if (BuildConfig.DEBUG && (node != m_head))
                            throw new AssertionError();
                        node.next.prev = null;
                        m_head = node.next;
                    }
                    else
                    {
                        // somewhere inside the list
                        if (BuildConfig.DEBUG && ((node == m_head) || (node == m_tail)))
                            throw new AssertionError();
                        node.prev.next = node.next;
                        node.next.prev = node.prev;
                    }

                    if (BuildConfig.DEBUG)
                    {
                        node = node.next;
                        while (node != null)
                        {
                            if (node.session == channelSession)
                                throw new AssertionError();
                            node = node.next;
                        }
                    }
                    return;
                }
                node = node.next;
            }

            if (BuildConfig.DEBUG)
            {
                // not found, should not happen
                throw new AssertionError();
            }
        }
        finally
        {
            m_lock.unlock();
        }
    }

    void sendAudioFrame(RetainableByteBuffer msg, boolean ptt)
    {
        Node node = m_head;
        while (node != null)
        {
            node.session.sendAudioFrame( msg, ptt );
            node = node.next;
        }
    }
}
