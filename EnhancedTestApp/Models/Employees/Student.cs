using System;
using System.Collections.Generic;
using System.Text;

namespace EnhancedTestApp.Models.Employees
{
    internal class Student
    {
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Patronymic { get; set; }
        public DateTime Birthday { get; set; }
        public double Rating { get; set; }

    }
    internal class Group 
    {
        public string Name { get; set; }
        public IList<Student> Students { get; set; }
    }
}
