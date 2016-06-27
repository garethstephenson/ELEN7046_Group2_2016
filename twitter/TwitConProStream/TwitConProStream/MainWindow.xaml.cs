using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace TwitConProStream
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        TwitterStreamConnectionManager streamManager;
        TwitConProLogger logger;
        MongoDBManager mongoDBManager;

        string[] terms = { "clinton", "trump" };

        public MainWindow()
        {
            InitializeComponent();

            logger = new TwitConProLogger(lstvwEvent, lstvwStream, lstvwDataBase);

            mongoDBManager = new MongoDBManager(logger);
            streamManager = new TwitterStreamConnectionManager(mongoDBManager, logger, terms);
        }

        private void btnConnect_Click(object sender, RoutedEventArgs e)
        {
            streamManager.StartStream();
        }

        private void btnDisconnect_Click(object sender, RoutedEventArgs e)
        {
            streamManager.EndStream();
        }

        private void btnDumpToTextFile_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                mongoDBManager.DumpDB(txtbxDumpDir.Text);
            }
            catch (Exception ex)
            {
                logger.LogEvent("Unable to Dump the database contents to the directory " + txtbxDumpDir.Text + ", message was: " + ex.Message);
            }
        }
    }
}
