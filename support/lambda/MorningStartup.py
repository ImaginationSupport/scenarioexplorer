# cron(0 12 ? * MON-FRI *)

# 06:00 EDT = 12:00 UTC

import boto3

region = 'us-east-1'

instances = [
    'i-04dd904e4c2630768',  # Production
    # 'i-0303313443cbb5f42',  # Dev
    'i-0c963aa856e1be563'  # Dev 2
]


def lambda_handler(event, context):
    print('Starting: ', instances)

    ec2 = boto3.client('ec2', region_name=region)
    ec2.start_instances(InstanceIds=instances)

    return
