package algorithms.vericom.model;

/**
 * @author LinX
 */
public enum ExperienceQuestionType {
    LANGUAGE( "Language Skills" ),
    SOFTWARE_PROJECT( "Software Project Skills" ),
    QUALITY_ASSURANCE( "Quality Assurance Skills" ),
    WORKING_ENVIRONMENT( "Working Environment" ),
    DOMAIN_EXPERIENCE( "Domain Experience" ),
    CROWDSOURCING_APPLICATIONS( "Crowdsourcing Applications Experience" );

    private final String name;

    ExperienceQuestionType( final String name ) {
        this.name = name;
    }
}
