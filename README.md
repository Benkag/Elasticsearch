# Elasticsearch


## ✅ Introduction

In modern information systems, servers generate a large amount of log data every second to record their real-time activities, status updates, and user interactions. Analyzing these log files efficiently is crucial for system administrators, especially when they need to detect important events or troubleshoot system behavior. However, when the number of log files becomes large, sequential processing becomes slow and impractical.

This project focuses on designing and implementing a parallel solution for generating and analyzing server log files using Java multithreading. The input of the assignment consists of 3,000 log files, each containing 20,000 lines of text. Every log file records various server events, and our goal is to search for the specific keyword “login by 99” inside all files. The objective is to identify:

Which file contains the keyword

The exact line number where the keyword appears

The content of that log line

To achieve this, the project is divided into two programs:

Program 1 – Log Generator:
Uses multiple threads to generate 3,000 log files with realistic server events. Each thread is responsible for creating a portion of the files, ensuring faster execution compared to sequential file creation.

Program 2 – Parallel Log Search:
Uses Java threads to divide the 3,000 log files among multiple worker threads. Each thread scans its assigned files and records the lines containing “login by 99”. The results are safely collected into a shared data structure and written to an output file named ketqua.txt.

By applying multithreading techniques, the project demonstrates how parallel algorithms can significantly improve performance when dealing with large datasets. The implementation also showcases important concepts such as workload distribution, thread synchronization, thread-safe data structures, and I/O optimization.

This assignment highlights the practical value of parallel programming in real-world scenarios where high-volume log processing is required.

## ✅ Conclusion

In this project, we successfully applied parallel programming techniques to solve a large-scale log generation and search problem. By using Java multithreading, both tasks—creating 3,000 log files and searching for a specific keyword across all files—were executed significantly faster than a traditional sequential approach.

The parallel file generator demonstrated how multiple threads can efficiently divide the workload, reducing the total execution time for producing large data sets. Similarly, the parallel log search program showed clear benefits of concurrency by allowing multiple threads to scan thousands of files simultaneously. By utilizing thread-safe data structures and proper workload partitioning, the system ensured correctness, stability, and high performance.

This assignment illustrates the importance of parallel computing in processing large volumes of data commonly found in real-world systems, such as server monitoring, cybersecurity analysis, and big data applications. The results affirm that parallel algorithms play a crucial role in improving efficiency and scalability, especially in environments with multi-core processors.

## ✅ System Architecture Diagram
                   +-----------------------------+
                   |         User / Admin        |
                   +--------------+--------------+
                                  |
                                  v
                 +-------------------------------------+
                 |       Program 1: Log Generator      |
                 +----------------+--------------------+
                                  |
                 +----------------+--------------------+
                 | Creates 3000 log files using        |
                 | multiple WriterThreads              |
                 +-------+---------------+-------------+
                         |               |
                     (Thread 1)      (Thread 2)...(Thread N)
                         |               |
                   +-----+-------+-------+-------------------+
                   |     logs/ directory (3000 files)        |
                   +-----------------+------------------------+
                                     |
                                     v
                 +-------------------------------------+
                 |   Program 2: Parallel Log Search    |
                 +----------------+--------------------+
                                  |
                     Splits 3000 files among threads
                                  |
           +----------------------+------------------------+
           |                 SearchThreads                 |
           +----------------------+------------------------+
                                  |
                                  v
          +------------------------------------------------+
          | Thread-safe Result Collection (Queue)         |
          +------------------------------------------------+
                                  |
                                  v
                 +-------------------------------------+
                 |         Output: ketqua.txt          |
                 +-------------------------------------+


## ✅ Flowchart – Program 1 (Log Generator)
         +------------------------+
         | Start Program 1       |
         +----------+-------------+
                    |
                    v
     +------------------------------------+
     | Create "logs" directory if missing |
     +------------------+-----------------+
                        |
                        v
         +-------------------------------+
         | Determine number of threads   |
         +------------------+------------+
                        |
                        v
     +------------------------------------+
     | Divide 3000 files among threads    |
     +------------------+-----------------+
                        |
                        v
         +-------------------------------+
         | Start WriterThread[i]         |
         +------------------+------------+
                        |
                        v
      +-------------------------------------+
      | Each thread creates assigned files  |
      | and writes 20,000 log lines         |
      +-----------------+-------------------+
                        |
                        v
         +-------------------------------+
         | All threads finished?         |
         +------------------+------------+
                    | Yes
                    v
        +------------------------------+
        | Print “Log creation done”    |
        +------------------------------+

## ✅ Flowchart – Program 2 (Parallel Log Search)
            +---------------------------+
            | Start Program 2          |
            +------------+--------------+
                         |
                         v
        +---------------------------------------+
        | Load list of 3000 log files           |
        +------------------+--------------------+
                         |
                         v
            +------------------------------+
            | Determine number of threads  |
            +------------------+-----------+
                         |
                         v
        +--------------------------------------+
        | Divide files evenly among threads    |
        +------------------+-------------------+
                         |
                         v
            +------------------------------+
            | Start SearchThread[i]        |
            +------------------+-----------+
                         |
                         v
     +----------------------------------------------------+
     | Each thread scans its files line by line           |
     | If line contains "login by 99" → store to queue    |
     +------------------+---------------------------------+
                         |
                         v
            +------------------------------+
            | All threads finished?        |
            +------------------+-----------+
                    | Yes
                    v
     +----------------------------------------------+
     | Write all results from queue to ketqua.txt   |
     +----------------------------------------------+
                    |
                    v
            +--------------------------+
            | Print “Search complete” |
            +--------------------------+


##🎬 DEMO – Parallel Log Generator & Log Search
-Parallel Log Generator-

<img width="1374" height="495" alt="image" src="https://github.com/user-attachments/assets/5e501d71-e772-44c7-8e2e-95653edadb8c" />


<img width="609" height="869" alt="image" src="https://github.com/user-attachments/assets/8d143124-afa6-42fb-9374-f1ed482af430" />


-Log Search-
<img width="1374" height="558" alt="image" src="https://github.com/user-attachments/assets/5d48203a-1815-45cd-b435-89870db87baf" />


<img width="1919" height="957" alt="image" src="https://github.com/user-attachments/assets/3fb4a480-cbc6-48d4-9175-ec8040467eb3" />


## SearchUI
<img width="788" height="491" alt="image" src="https://github.com/user-attachments/assets/159e05d4-db62-4453-b71c-5aa0b86ab626" />

Open Folder
<img width="507" height="355" alt="image" src="https://github.com/user-attachments/assets/cffb704a-3299-4f60-acdc-ff2e706a4ceb" />


<img width="782" height="490" alt="image" src="https://github.com/user-attachments/assets/7acebeae-e12c-441d-9f4b-85340d21d092" />







  

