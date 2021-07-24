using EnhancedTestApp.ViewModels.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace EnhancedTestApp.ViewModels
{
    class DbDataViewModel : ViewModel
    {
        private string _Title = "Главное меню";
        public string Title {get => _Title; set => Set(ref _Title, value); }
    }
}
