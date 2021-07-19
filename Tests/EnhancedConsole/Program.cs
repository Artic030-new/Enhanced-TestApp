using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Linq;
using System.Threading.Tasks;
using System.Globalization;

namespace EnhancedConsole
{
    class Program
    {
        private const string data_url = @"https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

        private static async Task<Stream> GetDataAsync()
        {
            var client = new HttpClient();
            var responce = await client.GetAsync(data_url, HttpCompletionOption.ResponseHeadersRead);
            return await responce.Content.ReadAsStreamAsync();
        
        }
        private static IEnumerable<string> GetDataLines() 
        {
            using var data_stream = GetDataAsync().Result;
            using var data_reader = new StreamReader(data_stream);
            while (!data_reader.EndOfStream)
            {
                var line = data_reader.ReadLine();
                if (string.IsNullOrWhiteSpace(line))
                    continue;
                yield return line.Replace("Korea,", "Korea -");
            }
        }

        private static DateTime[] GetDates() => GetDataLines().First().Split(',').Skip(4).Select(s => DateTime.Parse(s, CultureInfo.InvariantCulture)).ToArray();
        private static IEnumerable<(String Country, String Province, int[] Counts)> GetData() 
        {
            var lines = GetDataLines().Skip(1).Select(line => line.Split(','));
            foreach (var item in lines)
            {
                var province = item[0].Trim();
                var country_name = item[1].Trim(' ', '"');
                var counts = item.Skip(4).Select(int.Parse).ToArray();

                yield return (province, country_name, counts);
            }
        }
        static void Main(string[] args)
        {
            foreach (var item in GetDataLines())
            {
                // Console.WriteLine(item);
                var dates = GetDates();
                Console.WriteLine(String.Join("\r\n", dates));
            }
        }
    }
}
