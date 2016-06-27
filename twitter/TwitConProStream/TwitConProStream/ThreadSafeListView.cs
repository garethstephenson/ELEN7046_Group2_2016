using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace TwitConProStream
{
    public static class ListViewExtension
    {
        public static void AddItem(this ListView value, string item)
        {
            value.Items.Add(item);
        }
    }
}
