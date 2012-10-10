modules = {
    ezproxy {
        dependsOn "jquery-ui"
        resource id:"js", url: [plugin: "metridocEzproxy", dir: 'js', file: 'ezproxy.js'],
            attrs: [type: 'js']
        resource id:"css", url: [plugin: "metridocEzproxy", dir: 'css', file: 'ezproxy.css'],
            attrs: [type: 'css']
    }
}