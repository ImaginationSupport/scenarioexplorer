import datetime
import boto3
import dateutil
import pytz as pytz

################################################################################


TAG_AUTO_START = 'AutoStart'
TAG_AUTO_STOP = 'AutoStop'

TAG_NEXT_AUTO_ACTION = 'NextAutoAction'

################################################################################

# now_local = datetime.datetime.now()
# timezone = pytz.timezone("America/Los_Angeles")
# now_remote = timezone.localize(now_local)
#
# print(now_local.strftime("%Y-%m-%d %H:%M:%S"))
# print(now_remote.strftime("%Y-%m-%d %H:%M:%S"))

# utc_now = pytz.utc.localize(datetime.datetime.utcnow())
# print(utc_now.strftime("%Y-%m-%d %H:%M:%S"))
#
# est_now = utc_now.astimezone(pytz.timezone("America/New_York"))
# print(est_now.strftime("%Y-%m-%d %H:%M:%S"))
#
# time_code_now = est_now.strftime("%H:%M")
# print(time_code_now)

################################################################################

# ec2 = boto3.resource('ec2')
#
# instances = ec2.instances.filter()
#
# print()
# print('Current tags:')
# for instance in instances:
#     print('\t%s' % instance.id)
#     for tag in instance.tags:
#         print('\t\t%-12s %s' % (tag['Key'], tag['Value']))

################################################################################
# figure out the next auto action

# print()
# print('Determine next auto action:')
# for instance in instances:
#     print('instance: %s' % instance.id)
#     auto_start = None
#     auto_stop = None
#     for tag in instance.tags:
#         print('    %-20s%s' % (tag['Key'], tag['Value']))

################################################################################
# process the auto actions

# time_code_now = pytz.utc.astimezone(pytz.timezone("America/New_York")).strftime("%H:%M")
# print(time_code_now)

# print(datetime(2002, 10, 27, 12, 0, 0, tzinfo=datetime.timezone.).strftime("%H:%M"))

utc_now = datetime.datetime.utcnow()
print(utc_now.strftime("%H:%M"))

to_timezone = dateutil.tz.gettz('America/New_York')
print(to_timezone)
local_now = utc_now.astimezone(to_timezone)
print(local_now.strftime("%H:%M"))

# print()
# print('Process next auto action:')
# for instance in instances:
#     print('instance: %s' % instance.id)
#     auto_action = None
#     for tag in instance.tags:
#         if tag['Key'] == TAG_NEXT_AUTO_ACTION and len(tag['Value']) > 0:
#             auto_action = tag['Value']
#     print('    %s: %s' % (TAG_NEXT_AUTO_ACTION, auto_action))

################################################################################
