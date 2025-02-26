-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.batchManagement1.0-messaging3.0
visibility=private
IBM-Provision-Capability: \
 osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=io.openliberty.messaging-3.0.internal))", \
 osgi.identity; filter:="(&(type=osgi.subsystem.feature)(osgi.identity=com.ibm.websphere.appserver.batchManagement-1.0))"
-features=io.openliberty.mdb-4.0, \
  com.ibm.websphere.appserver.transaction-2.0
-bundles=com.ibm.ws.jbatch.jms.jakarta
IBM-Install-Policy: when-satisfied
kind=ga
edition=base
WLP-Activation-Type: parallel
