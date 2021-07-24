using Microsoft.Extensions.Hosting;
using System;
using System.Collections.Generic;
using System.Text;

namespace EnhancedTestApp
{
    class Program
    {
        [STAThread]
        static void Main(string[] args) 
        {
            var app = new App();
            app.InitializeComponent();
            app.Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) => Host.CreateDefaultBuilder(args).ConfigureServices(App.ConfigureServices);
    }
}
