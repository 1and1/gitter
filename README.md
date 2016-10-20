# Gitter

Gitter is a Git statistics tool. It can process multiple repositories
and report statistics or commit traffic on them.

## Command line options

The command line options are listed here:

     DIR                                    : GIT repository dirs.
     -I (--interval) [YEARS | MONTHS |      : The time interval to use.
     DAYS]                                     
     -f (--from) DATE                       : From timestamp to use for filtering,
                                              for example 2015-12-31.
     -h (--help)                            : Show this help message.
     -o (--output) FILE                     : The output file.
     -p (--pattern) REGEX                   : Short message pattern.
     -r (--report) [COMMITS_PER_INTERVAL |  : The flavor of report.
     AUTHORS_PER_INTERVAL | AUTHORS_COMMITS    
     _PER_INTERVAL | DAYTIMES_PER_INTERVAL     
     | MESSAGE_PATTERN_PER_INTERVAL]           
     -t (--to) DATE                         : To timestamp to use for filtering,
                                              for example 2015-12-31.


## Reports

This section shows an overview of available reports (aka flavors in the options).

### COMMITS_PER_INTERVAL

Shows the number of commits per project and per interval. 

### AUTHORS_PER_INTERVAL

Shows which authors were active on which project in which interval.
The author names are shortened to their initials.

### AUTHORS_COMMITS__PER_INTERVAL

Similar to AUTHORS_PER_INTERVAL, but also shows the number of commits per author.

### DAYTIMES_PER_INTERVAL

At which times do the commits happen every day?

### MESSAGE_PATTERN_PER_INTERVAL

Matches a regular expression against each commit message and shows the 
resulting matches per time interval. This report is useful for extracting
ticket numbers from commit messages and displaying them per project.

## License

Gitter is under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
