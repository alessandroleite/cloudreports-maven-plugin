package cloudreport.maven.plugin;

import static cloudreport.maven.plugin.util.Reflections.*;

public enum ExtensionTypes {

	/**
	 * Virtual machines allocation policies
	 */
	VM_ALLOCATION_POLICY("VmAllocationPolicy",
			"cloudreports.extensions.vmallocationpolicies.VmAllocationPolicyExtensible"),

	/**
	 * Broker policies
	 */
	BROKER_POLICY("Broker", "cloudreports.extensions.brokers.Broker"),

	/**
	 * Processing elements provisioner
	 */
	PE_PROVISIONER("PeProvisioner",
			"org.cloudbus.cloudsim.provisioners.PeProvisioner"),

	/**
	 * RAM provisioner
	 */
	RAM_PROVISIONER("RamProvisioner",
			"org.cloudbus.cloudsim.provisioners.RamProvisioner"),

	/**
	 * Bandwidth provisioner
	 */
	BW_PROVISIONER("BwProvisioner",
			"org.cloudbus.cloudsim.provisioners.BwProvisioner"),

	/**
	 * Cloudlets schedulers
	 */
	CLOUDLET_SCHEDULER("CloudletScheduler",
			"org.cloudbus.cloudsim.CloudletScheduler"),

	/**
	 * Power consumption models
	 */
	POWER_MODEL("PowerModel", "org.cloudbus.cloudsim.power.models.PowerModel"),

	/**
	 * Resource utilization models
	 */
	UTILIZATION_MODEL("UtilizationModel",
			"org.cloudbus.cloudsim.UtilizationModel"),

	/**
	 * Virtual machines schedulers
	 */
	VM_SCHEDULE("VmScheduler", "org.cloudbus.cloudsim.VmScheduler");

	private final String type;
	private final String classAssignableFrom;

	private ExtensionTypes(String type, String classAssignableFrom) 
	{
		this.type = type;
		this.classAssignableFrom = classAssignableFrom;
	}
	
	public static ExtensionTypes valueOf(Class<?> clazz)
	{
		return valueOf(clazz, ExtensionTypes.class.getClassLoader());
	}

	public static ExtensionTypes valueOf(Class<?> clazz, ClassLoader loader) 
	{
		if (clazz != null) 
		{
			for (ExtensionTypes type : ExtensionTypes.values()) 
			{
				if (asClass(type.getClassAssignableFrom(), loader).isAssignableFrom(clazz)) 
				{
					return type;
				}
			}
		}
		return null;
	}

	public static boolean isClassAssignableFrom(Class<?> clazz) 
	{
		return isClassAssignableFrom(clazz, ExtensionTypes.class.getClassLoader());
	}
	
	public static boolean isClassAssignableFrom(Class<?> clazz, ClassLoader loader) 
	{
		ExtensionTypes type = valueOf(clazz, loader);
		return type != null;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the classAssignableFrom
	 */
	public String getClassAssignableFrom() {
		return classAssignableFrom;
	}
}