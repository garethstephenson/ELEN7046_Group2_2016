using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TwitConProStream
{

    public class Coordinates
    {
        //Lat and Long
        private double latitude = 0.0;
        private double longitude = 0.0;

        public Coordinates(double _lat, double _long)
        {
            latitude = _lat;
            longitude = _long;
        }

        public static Coordinates DefaultCoordinates()
        {
            return new Coordinates(0, 0);
        }

        public double Latitude
        {
            get { return latitude; }
        }

        public double Longitude
        {
            get { return longitude; }
        }

    }

}
