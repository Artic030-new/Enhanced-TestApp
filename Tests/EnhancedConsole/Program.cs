using System;
using System.Net.Http;

namespace EnhancedConsole
{
    class Program
    {
        private const string data_url = @"https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

        static void Main(string[] args)
        {
            var client = new HttpClient();
            var csv_str = client.GetAsync(data_url).Result;
            var csv_str2 = csv_str.Content.ReadAsStringAsync().Result;
        }
    }
}
