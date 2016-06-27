using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Windows.Controls;

namespace TwitConProStream
{
    public class ListboxLogger
    {
        ListView outListBox;

        public ListboxLogger(ListView _outListBox)
        {
            outListBox = _outListBox;
        }

        public void WriteOutLine(string message)
        {
            outListBox.Dispatcher.Invoke((Action)( () => { outListBox.Items.Add(message); }));
        }
    }
}
