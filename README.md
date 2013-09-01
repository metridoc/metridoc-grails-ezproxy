WARNING - Still in heavy development

This project contains both the ezproxy web and job projects.  We are in the process of moving the job
[here](https://github.com/metridoc/metridoc-job-ezproxy) using our new scripting api, but for now both the job and view 
reside in teh same project

#### Ezproxy Job

The ezproxy job provides a way to parse and store ezproxy data.  The most effective way of using the job is to make sure
your target log has items separated by one or more uncommon characters.  If this is not possible, You can either 
preprocess your logs into this format or provide a parser of your own.

To get started, download the project.  You can either download the zip file or clone it if git is installed

```bash
git clone https://github.com/metridoc/metridoc-grails-ezproxy.git
cd metridoc-grails-ezproxy
```

There are 4 available jobs

*  `processEzHosts` - processes all unique host names based on ezproxy id
*  `processEzDois` - processes all unique dois based on ezproxy ids
*  `resolveEzDois` - resolves dois against crossref
*  `dropEzTables` - deletes all ezproxy tables.  Useful if you want to start fresh, or
substantial changes have occurred to the database schema after an upgrade that a fresh start is required



