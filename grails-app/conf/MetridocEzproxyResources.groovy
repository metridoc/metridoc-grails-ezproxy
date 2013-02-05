modules = {
    ezproxy {
        resource id: "js", url: [plugin: "metridocEzproxy", dir: 'js', file: 'ezproxy.js']
        resource id: "css", url: [plugin: "metridocEzproxy", dir: 'css', file: 'ezproxy.css'],
                disposition: "defer"
    }
}