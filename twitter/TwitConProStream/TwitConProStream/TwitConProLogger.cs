using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Windows.Controls;

namespace TwitConProStream
{
    public class TwitConProLogger
    {
        ListboxLogger eventLogger;
        ListboxLogger streamLogger;
        ListboxLogger dbLogger;

        public TwitConProLogger(ListView _event, ListView _stream, ListView _db)
        {
            eventLogger = new ListboxLogger(_event);
            streamLogger = new ListboxLogger(_stream);
            dbLogger = new ListboxLogger(_db);
        }

        public void LogEvent(string msg)
        {
            eventLogger.WriteOutLine(msg);
        }

        public void LogStream(string msg)
        {
            streamLogger.WriteOutLine(msg);
        }

        public void LogDBMessage(string msg)
        {
            dbLogger.WriteOutLine(msg);
        }
    }
}
