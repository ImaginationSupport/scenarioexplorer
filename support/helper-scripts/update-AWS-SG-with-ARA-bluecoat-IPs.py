#!/usr/bin/env python

import boto3
import math

############################################################################################################################################

READ_ONLY = False

AWS_PROFILE = 'imaginationsupport'
AWS_REGION = 'us-east-1'

MAX_RULES_PER_GROUP = 50

VPC_ID = 'vpc-b3b435d6'

############################################################################################################################################


# http://bluecoat.force.com/knowledgebase/articles/Solution/KB4583
# updated 2018-01-15

EXIT_POINTS = [
    # Chicago, IL
    ('Chicago, IL - DP1', '198.135.124.0/27', '198.135.124.1', '198.135.124.30'),
    ('Chicago, IL - DP2', '198.135.124.32/27', '198.135.124.33', '198.135.124.62'),
    ('Chicago, IL - DP3', '198.135.124.64/27', '198.135.124.65', '198.135.124.94'),
    ('Chicago, IL - DP4', '198.135.125.0/27', '198.135.125.1', '198.135.125.30'),
    ('Chicago, IL - DP5', '198.135.125.32/27', '198.135.125.33', '198.135.125.62'),
    ('Chicago, IL - DP6', '198.135.125.64/27', '198.135.125.65', '198.135.125.94'),
    ('Chicago, IL - DP7', '198.135.125.96/27', '198.135.125.97', '198.135.125.126'),
    ('Chicago, IL - DP8', '198.135.125.128/27', '198.135.125.129', '198.135.125.158'),
    ('Chicago, IL - DP9', '198.135.125.160/27', '198.135.125.161', '198.135.125.190'),
    ('Chicago, IL - DP10', '198.135.124.96/27', '198.135.124.97', '198.135.124.126'),
    ('Chicago, IL - DP11', '198.135.124.192/27', '198.135.124.193', '198.135.124.222'),
    ('Chicago, IL - DP12', '199.116.173.0/27', '199.116.173.1', '199.116.173.30'),
    ('Chicago, IL - DP13', '199.116.173.32/27', '199.116.173.33', '199.116.173.62'),
    ('Chicago, IL - DP14', '199.116.173.64/27', '199.116.173.65', '199.116.173.94'),
    ('Chicago, IL - DP15', '199.116.173.96/27', '199.116.173.97', '199.116.173.126'),
    ('Chicago, IL - DP16', '199.116.173.160/27', '199.116.173.161', '199.116.173.190'),

    # Dallas, TX
    ('Dallas, TX - DP1', '199.116.171.0/27', '199.116.171.1', '199.116.171.30'),
    ('Dallas, TX - DP2', '199.116.171.32/27', '199.116.171.33', '199.116.171.62'),
    ('Dallas, TX - DP3', '199.116.171.64/27', '199.116.171.65', '199.116.171.94'),
    ('Dallas, TX - DP4', '199.116.171.96/27', '199.116.171.97', '199.116.171.126'),
    ('Dallas, TX - DP5', '199.116.171.192/27', '199.116.171.193', '199.116.171.222'),
    ('Dallas, TX - DP6', '199.19.252.0/27', '199.19.252.1', '199.19.252.30'),
    ('Dallas, TX - DP7', '199.19.252.32/27', '199.19.252.33', '199.19.252.62'),
    ('Dallas, TX - DP8', '199.19.252.64/27', '199.19.252.65', '199.19.252.94'),
    ('Dallas, TX - DP9', '199.19.252.96/27', '199.19.252.97', '199.19.252.126'),
    ('Dallas, TX - DP10', '199.19.252.128/27', '199.19.252.129', '199.19.252.158'),

    # Denver, CO
    ('Denver, CO - DP1', '8.39.233.0/27', '8.39.233.1', '8.39.233.30'),
    ('Denver, CO - DP2', '8.39.233.32/27', '8.39.233.33', '8.39.233.62'),
    ('Denver, CO - DP3', '8.39.233.64/27', '8.39.233.65', '8.39.233.94'),
    ('Denver, CO - DP4', '8.39.233.96/27', '8.39.233.97', '8.39.233.126'),
    ('Denver, CO - DP5', '8.39.233.192/27', '8.39.233.193', '8.39.233.222'),

    # Miami, FL
    ('Miami, FL - DP1', '199.19.251.0/27', '199.19.251.1', '199.19.251.30'),
    ('Miami, FL - DP2', '199.19.251.32/27', '199.19.251.33', '199.19.251.62'),
    ('Miami, FL - DP3', '199.19.251.64/27', '199.19.251.65', '199.19.251.94'),
    ('Miami, FL - DP4', '199.19.251.96/27', '199.19.251.97', '199.19.251.126'),

    # New York, NY
    ('New York, NY - DP1', '199.116.175.0/27', '199.116.175.1', '199.116.175.30'),
    ('New York, NY - DP2', '199.116.175.32/27', '199.116.175.33', '199.116.175.62'),
    ('New York, NY - DP3', '199.116.175.64/27', '199.116.175.65', '199.116.175.94'),
    ('New York, NY - DP4', '199.116.175.96/27', '199.116.175.97', '199.116.175.126'),
    ('New York, NY - DP5', '199.116.175.192/27', '199.116.175.193', '199.116.175.222'),

    # Seattle, WA
    ('Seattle, WA - DP1', '199.116.168.0/27', '199.116.168.1', '199.116.168.30'),
    ('Seattle, WA - DP2', '199.116.168.32/27', '199.116.168.33', '199.116.168.62'),
    ('Seattle, WA - DP3', '199.116.168.64/27', '199.116.168.65', '199.116.168.94'),
    ('Seattle, WA - DP4', '199.116.168.96/27', '199.116.168.97', '199.116.168.126'),
    ('Seattle, WA - DP5', '199.116.169.0/27', '199.116.169.1', '199.116.169.30'),
    ('Seattle, WA - DP6', '199.116.169.32/27', '199.116.169.33', '199.116.169.62'),
    ('Seattle, WA - DP7', '199.116.169.64/27', '199.116.169.65', '199.116.169.94'),
    ('Seattle, WA - DP8', '199.116.169.96/27', '199.116.169.97', '199.116.169.126'),
    ('Seattle, WA - DP9', '199.116.169.128/27', '199.116.169.129', '199.116.169.158'),
    ('Seattle, WA - DP10', '38.72.131.0/27', '38.72.131.1', '38.72.131.30'),
    ('Seattle, WA - DP11', '38.72.131.32/27', '38.72.131.33', '38.72.131.62'),
    ('Seattle, WA - DP12', '38.72.131.64/27', '38.72.131.65', '38.72.131.94'),
    ('Seattle, WA - DP13', '38.72.131.96/27', '38.72.131.97', '38.72.131.126'),
    ('Seattle, WA - DP14', '38.72.131.128/27', '38.72.131.129', '38.72.131.158'),
    ('Seattle, WA - DP15', '38.72.131.160/27', '38.72.131.161', '38.72.131.190'),

    # Sunnyvale, CA
    ('Sunnyvale, CA - DP1', '199.19.248.0/27', '199.19.248.1', '199.19.248.30'),
    ('Sunnyvale, CA - DP2', '199.19.248.32/27', '199.19.248.33', '199.19.248.62'),
    ('Sunnyvale, CA - DP3', '199.19.248.64/27', '199.19.248.65', '199.19.248.94'),
    ('Sunnyvale, CA - DP4', '199.19.248.96/27', '199.19.248.97', '199.19.248.126'),
    ('Sunnyvale, CA - DP5', '199.19.248.192/27', '199.19.248.193', '199.19.248.222'),

    # Washington, DC
    ('Washington, DC - DP1', '199.19.250.0/27', '199.19.250.1', '199.19.250.30'),
    ('Washington, DC - DP2', '199.19.250.32/27', '199.19.250.33', '199.19.250.62'),
    ('Washington, DC - DP3', '199.19.250.64/27', '199.19.250.65', '199.19.250.94'),
    ('Washington, DC - DP4', '199.19.250.96/27', '199.19.250.97', '199.19.250.126'),
    ('Washington, DC - DP5', '199.116.174.0/27', '199.116.174.1', '199.116.174.30'),
    ('Washington, DC - DP6', '199.116.174.32/27', '199.116.174.33', '199.116.174.62'),
    ('Washington, DC - DP7', '199.116.174.64/27', '199.116.174.65', '199.116.174.94'),
    ('Washington, DC - DP8', '199.116.174.96/27', '199.116.174.97', '199.116.174.126'),
    ('Washington, DC - DP9', '199.116.174.160/27', '199.116.174.161', '199.116.174.190'),
    # DP10 (Not in rotation)
    ('Washington, DC - DP11', '38.68.203.0/27', '38.68.203.1', '38.68.203.30'),
    ('Washington, DC - DP12', '38.68.203.32/27', '38.68.203.33', '38.68.203.62'),
    ('Washington, DC - DP13', '38.68.203.64/27', '38.68.203.65', '38.68.203.94'),
    ('Washington, DC - DP14', '38.68.203.96/27', '38.68.203.97', '38.68.203.126'),
    ('Washington, DC - DP15', '38.134.125.0/27', '38.134.125.1', '38.134.125.30'),
    ('Washington, DC - DP16', '38.134.125.32/27', '38.134.125.33', '38.134.125.62'),
    ('Washington, DC - DP17', '38.134.125.64/27', '38.134.125.65', '38.134.125.94'),
    ('Washington, DC - DP18', '38.134.125.96/27', '38.134.125.97', '38.134.125.126'),
    ('Washington, DC - DP19', '38.134.125.160/27', '38.134.125.161', '38.134.125.190'),
    ('Washington, DC - DP20', '38.134.125.224/27', '38.134.125.225', '38.134.125.254'),

    # Custom
    ('ARA wifi', '63.239.195.245/32', None, None),
    ('ATA', '98.175.25.62/32', None, None),

    # ARA private network (i.e. SSH): 63.239.195.254
]

############################################################################################################################################

def main():
    if READ_ONLY:
        print( '####################' )
        print( '##                ##' )
        print( '## READ ONLY MODE ##' )
        print( '##                ##' )
        print( '####################\n' )

    print( 'Connecting to AWS EC2...' )
    session = boto3.Session( profile_name=AWS_PROFILE, region_name=AWS_REGION )
    ec2 = session.resource( 'ec2' )
    print( '\tdone.' )
    print( '' )

    num_groups_needed = int( math.ceil( len( EXIT_POINTS ) / MAX_RULES_PER_GROUP ) )
    print( 'Number of groups needed: %d\n' % num_groups_needed )

    create_groups( ec2, 'HTTP', 80, num_groups_needed )
    create_groups( ec2, 'HTTPS', 443, num_groups_needed )
    # create_groups( ec2, 'SSH', 22, num_groups_needed )

    print( 'all tasks complete.' )

    return

############################################################################################################################################

def create_groups( ec2, title, port, num_groups_needed ):
    print( 'Creating %s(port %d) groups...' % (title, port) )

    security_groups = [ ]

    existing_security_groups = ec2.security_groups.filter( Filters=[ ] )

    security_group_ids_to_populate = [ ]

    for i in range( 1, num_groups_needed + 1 ):
        security_group_name = '%s from whitelist - set %d of %d' % (title, i, num_groups_needed)

        found = False
        for existing_security_group in existing_security_groups:
            # print( '%-11s | %-40s | %s' % (existing_security_group.group_id, existing_security_group.group_name, existing_security_group.description) )
            if existing_security_group.group_name == security_group_name:
                found = True
                print( '\tGroup "%s" already exists.  Cleaning...' % security_group_name )
                clean_existing_rules( ec2, existing_security_group.group_id )
                security_group_ids_to_populate.append( existing_security_group.group_id )

        if not found:
            print( '\tGroup "%s" not found.  Creating...' % security_group_name )
            if not READ_ONLY:
                new_security_group = ec2.create_security_group( GroupName=security_group_name, Description='%s (TCP %d) from whitelist' % (title, port), VpcId=VPC_ID )
                security_group_ids_to_populate.append( new_security_group.group_id )

    populate_rules_into_group( ec2, port, security_group_ids_to_populate )

    print( '\tdone.' )
    print( '' )

    return security_groups

############################################################################################################################################


def clean_existing_rules( ec2, security_group_id ):
    security_group = ec2.SecurityGroup( security_group_id )

    for entry in security_group.ip_permissions:

        for ip_range in entry[ 'IpRanges' ]:
            cidr_ip = ip_range[ 'CidrIp' ]
            if 'Description' in ip_range:
                description = ip_range[ 'Description' ]
            else:
                description = ''
            port_from = int( entry[ u'FromPort' ] )
            # port_to = int(entry[u'ToPort'])
            print( '\t\tRemoving: %-20s %3s %s' % (cidr_ip, port_from, description) )

            if not READ_ONLY:
                security_group.revoke_ingress( IpProtocol='tcp', FromPort=port_from, ToPort=port_from, CidrIp=cidr_ip )

    return

############################################################################################################################################


def populate_rules_into_group( ec2, port, security_groups_ids ):
    if not security_groups_ids:
        raise Exception( 'No security groups!' )

    security_group_index = -1
    num_rules_in_current_security_group = 0
    security_group = None

    for exit_point in EXIT_POINTS:

        if security_group_index == -1 or num_rules_in_current_security_group == MAX_RULES_PER_GROUP:

            security_group_index += 1

            if len( security_groups_ids ) <= security_group_index:
                raise Exception( 'Not enough security group ids!' )

            security_group = ec2.SecurityGroup( security_groups_ids[ security_group_index ] )

            print( '\tUsing security group: %s' % security_groups_ids[ security_group_index ] )

        exit_point_name = exit_point[ 0 ]
        exit_point_cidr = exit_point[ 1 ]
        # exit_point_start = exit_point[2]
        # exit_point_end = exit_point[3]

        print( '\t\tAdding:   %-20s %3d = %s' % (exit_point_cidr, port, exit_point_name) )

        if not READ_ONLY:
            security_group.authorize_ingress(
                IpProtocol='tcp',
                FromPort=port,
                ToPort=port,
                CidrIp=exit_point_cidr,
            )

        num_rules_in_current_security_group += 1

    return

############################################################################################################################################

if __name__ == "__main__":
    main()

############################################################################################################################################
