package metridoc.ezproxy

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/20/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class EzproxyBase<T extends EzproxyBase> {
    abstract T createInstance(Map record)
    abstract T createDefaultInvalidRecord(Map record)
    abstract boolean accept(Map record)
}
