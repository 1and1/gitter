# Gitter

Gitter is a Git statistics tool. It can process multiple repositories
and report statistics or commit traffic on them.

The repositories must be checked out in the local file system for
Gitter to work.

## How it looks like

Here is an image showing the commits per interval of the open source git repositories of [Ansible](https://github.com/ansible/ansible) and [GoCD](https://github.com/gocd/gocd) in 2017:

![Commits per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/commits_per_interval.png "Commits per Interval")

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
    -r (--report) [COMMITS_PER_AUTHOR |    : The type of report to calculate.
    COMMITS_PER_INTERVAL | AUTHORS_PER_INT    
    ERVAL | AUTHORS_COMMITS_PER_INTERVAL      
    | DAYTIMES_PER_INTERVAL |                 
    MESSAGE_PATTERN_PER_AUTHOR |              
    MESSAGE_PATTERN_PER_INTERVAL |            
    PATCH_SCRIPT_SIZE_PER_INTERVAL]           
    -t (--to) DATE                         : To timestamp to use for filtering,
                                             for example 2015-12-31.
    -w (--committer) REGEX                 : The committer to filter for as a
                                             regex.


The *from* and *to* options and *DIR* arguments are always required.

## Reports

This section shows an overview of available reports (aka flavors in the options).

### COMMITS_PER_AUTHOR

Shows the number of commits per project and per author. 

![Commits per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/commits_per_interval.png "Commits per Interval")

### COMMITS_PER_INTERVAL

Shows the number of commits per project and per interval. 

![Commits per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/commits_per_interval.png "Commits per Interval")

### AUTHORS_PER_INTERVAL

Shows which authors were active on which project in which interval.
The author names are shortened to their initials.

In the following example the authors initials are taken. "Peter Huber" will have the initials "PH":

![Authors per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/authors_per_interval.png "Authors per Interval")

Please note that the abbreviation with name initials works best for a limited set of developers.

### AUTHORS_COMMITS__PER_INTERVAL

Similar to AUTHORS_PER_INTERVAL, but also shows the number of commits per author.

In the following example the authors initials are taken. "Peter Huber" will have the initials "PH":

![Authors commits per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/authors_commits_per_interval.png "Authors Commits per Interval")

### DAYTIMES_PER_INTERVAL

At which times do the commits happen every day?

![Daytimes per Interval](https://raw.githubusercontent.com/1and1/gitter/master/images/daytimes_per_interval.png "Daytimes per Interval")

### MESSAGE_PATTERN_PER_AUTHOR

Matches a regular expression against each commit message and shows the 
resulting matches per author. This report is useful for extracting
ticket numbers from commit messages and displaying them per project.

In the following example the issue numbers (#1234) of Github were taken as a pattern:

![Message pattern per author](https://raw.githubusercontent.com/1and1/gitter/master/images/message_pattern_per_author.png "Message patterns per author")

### MESSAGE_PATTERN_PER_INTERVAL

Matches a regular expression against each commit message and shows the 
resulting matches per time interval. This report is useful for extracting
ticket numbers from commit messages and displaying them per project.

In the following example the issue numbers (#1234) of Github were taken as a pattern:

![Message pattern per interval](https://raw.githubusercontent.com/1and1/gitter/master/images/message_pattern_per_interval.png "Message patterns per interval")

### PATCH_SCRIPT_SIZE_PER_INTERVAL

The patch script sizes in bytes per interval.
Note that the overall first commit is not counted correctly at the moment.

## License

Gitter is under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
