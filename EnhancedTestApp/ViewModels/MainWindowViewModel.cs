using EnhancedTestApp.Infrastructure.Commands;
using EnhancedTestApp.Models.Employees;
using EnhancedTestApp.ViewModels.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Input;

namespace EnhancedTestApp.ViewModels
{
    internal class MainWindowViewModel : ViewModel
    {
        public ObservableCollection<Group> Groups { get; }
        public MainWindowViewModel() 
        {
            #region === Команды ===
            CloseAppCommand = new LambdaCommand(OnCloseAppCommandExecuted, CanCloseAppCommandExecute);
            #endregion === Команды ===
            int student_index = 1;
            Random rnd = new Random();
            var students = Enumerable.Range(1, 10).Select(s => new Student 
            {
                Name = "Студент" + student_index,
                Surname = "Имя" + student_index, 
                Patronymic = "Отчество" + ++student_index,
                Birthday = DateTime.Now,
                Rating = Math.Floor(rnd.NextDouble())

            });
            var groups = Enumerable.Range(1, 20).Select(e => new Group() 
            {
                Name = "Группа"  + e,
                Students = new ObservableCollection<Student>(students),
            });;
            Groups = new ObservableCollection<Group>(groups);
            
        }
        #region Свойство заголовка
        /// <summary> Заголовок окна </summary>
        private string _Title = "Главное меню";
        public string Title
        {
            get => _Title;
            set => Set(ref _Title, value);
        }
        #endregion

        #region Свойство статус
        /// <summary> Заголовок окна </summary>
        private string _Status = "Something...";
        public string Status
        {
            get => _Status;
            set => Set(ref _Status, value);
        }
        #endregion

        #region === Команды ===

        #region === Завершение работы ===
        public ICommand CloseAppCommand { get; }
        private void OnCloseAppCommandExecuted(object o) => Application.Current.Shutdown();
        private bool CanCloseAppCommandExecute(object o) =>  true;
        #endregion === Завершение работы ===

        #endregion === Команды ===
    }
}
